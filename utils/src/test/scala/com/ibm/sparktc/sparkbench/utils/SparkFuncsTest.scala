/**
  * (C) Copyright IBM Corp. 2015 - 2017
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */

package com.ibm.sparktc.sparkbench.utils

import com.holdenkarau.spark.testing.Utils
import com.ibm.sparktc.sparkbench.testfixtures.SparkSessionProvider.spark
import java.io.File
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

class SparkFuncsTest extends FlatSpec with Matchers with BeforeAndAfterAll {

  import SparkFuncs._

  private var dir: File = _
  private var parquetDir: File = _

  override def beforeAll(): Unit = {
    dir = Utils.createTempDir()
    parquetDir = Utils.createTempDir()
  }

  "pathExists" should "behave when not prefixed with a filesystem" in {
    val f = new File(dir, "foo.txt")
    f.createNewFile()
    f.setLastModified(System.currentTimeMillis)
    pathExists(f.getAbsolutePath, spark) shouldBe true
  }

  it should "behave when prefixed with a filesystem" in {
    val f = new File(dir, "bar.txt")
    f.createNewFile()
    f.setLastModified(System.currentTimeMillis)
    pathExists(s"file://${f.getAbsolutePath}", spark) shouldBe true
  }

  it should "also work when the file is not present and no filesystem" in {
    pathExists(s"/tmp/fake_${System.currentTimeMillis}", spark) shouldBe false
  }

  it should "also work when the file is not present and filesystem" in {
    pathExists(s"file:///tmp/fake_${System.currentTimeMillis}", spark) shouldBe false
  }

  "writeToDisk" should "write to parquet on a local filesystem" in {
    import spark.sqlContext.implicits._
    val df = spark.sparkContext.parallelize(0 until 10).toDF()
    writeToDisk(parquetDir.getAbsolutePath, "overwrite", df, spark, Some(Formats.parquet))
  }

  it should "load from local filesystem" in {
    val df = load(spark, parquetDir.getAbsolutePath, Some(Formats.parquet))
    val data = df.collect.map(_.getAs[Int](0))
    data should have size 10
    data.sorted shouldBe (0 until 10).toArray
  }
}
