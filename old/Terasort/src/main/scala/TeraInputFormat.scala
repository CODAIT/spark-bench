/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package src.main.scala

import scala.collection.JavaConversions._
import scala.util.control.Breaks._

import java.io.EOFException
import java.io.IOException
import java.util.ArrayList
import java.util.List

import com.google.common.primitives.UnsignedBytes
import org.apache.hadoop.fs.FSDataInputStream
import org.apache.hadoop.fs.BlockLocation
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.FileStatus
import org.apache.hadoop.fs.LocatedFileStatus
import org.apache.hadoop.fs.Path
import org.apache.hadoop.mapreduce.InputSplit
import org.apache.hadoop.mapreduce.JobContext
import org.apache.hadoop.mapreduce.RecordReader
import org.apache.hadoop.mapreduce.TaskAttemptContext
import org.apache.hadoop.mapreduce.TaskAttemptContext
import org.apache.hadoop.mapreduce.TaskAttemptID
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.input.FileSplit
import org.apache.hadoop.mapreduce.task.TaskAttemptContextImpl
import org.apache.hadoop.util.IndexedSortable
import org.apache.hadoop.util.QuickSort
import org.apache.hadoop.util.StringUtils

import com.google.common.base.Stopwatch

object TeraInputFormat {
   val KEY_LEN = 10
   val VALUE_LEN = 90
   val RECORD_LEN = KEY_LEN + VALUE_LEN
   var lastContext : JobContext = null
   var lastResult : List[InputSplit] = null
   implicit val caseInsensitiveOrdering = UnsignedBytes.lexicographicalComparator
}

class TeraInputFormat extends FileInputFormat[Array[Byte], Array[Byte]] {

  override def createRecordReader(split: InputSplit, context: TaskAttemptContext)
  : RecordReader[Array[Byte], Array[Byte]] = new TeraRecordReader()

  // Sort the file pieces since order matters.
  override def listStatus(job: JobContext): List[FileStatus] = {
    val listing = super.listStatus(job)
    val sortedListing= listing.sortWith{ (lhs, rhs) => { 
      lhs.getPath().compareTo(rhs.getPath()) < 0 
    } }
    sortedListing.toList
  }

  class TeraRecordReader extends RecordReader[Array[Byte], Array[Byte]] {
    private var in : FSDataInputStream = null
    private var offset: Long = 0
    private var length: Long = 0
    private val buffer: Array[Byte] = new Array[Byte](TeraInputFormat.RECORD_LEN)
    private var key: Array[Byte] = null
    private var value: Array[Byte] = null

    override def nextKeyValue() : Boolean = {
      if (offset >= length) {
        return false
      }
      var read : Int = 0
      while (read < TeraInputFormat.RECORD_LEN) {
        var newRead : Int = in.read(buffer, read, TeraInputFormat.RECORD_LEN - read)
        if (newRead == -1) {
          if (read == 0) false
          else throw new EOFException("read past eof")
        }
        read += newRead
      }
      if (key == null) {
        key = new Array[Byte](TeraInputFormat.KEY_LEN)
      }
      if (value == null) {
        value = new Array[Byte](TeraInputFormat.VALUE_LEN)
      }
      buffer.copyToArray(key, 0, TeraInputFormat.KEY_LEN)
      buffer.takeRight(TeraInputFormat.VALUE_LEN).copyToArray(value, 0, TeraInputFormat.VALUE_LEN)
      offset += TeraInputFormat.RECORD_LEN
      true
    }

    override def initialize(split : InputSplit, context : TaskAttemptContext) = {
      val fileSplit = split.asInstanceOf[FileSplit]
      val p : Path = fileSplit.getPath()
      val fs : FileSystem = p.getFileSystem(context.getConfiguration())
      in = fs.open(p)
      val start : Long = fileSplit.getStart()
      // find the offset to start at a record boundary
      val reclen = TeraInputFormat.RECORD_LEN
      offset = (reclen - (start % reclen)) % reclen
      in.seek(start + offset)
      length = fileSplit.getLength()
    }

    override def close() = in.close()
    override def getCurrentKey() : Array[Byte] = key
    override def getCurrentValue() : Array[Byte] = value
    override def getProgress() : Float = offset / length
  }

}