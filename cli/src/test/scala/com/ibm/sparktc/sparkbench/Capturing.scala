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

package com.ibm.sparktc.sparkbench

import java.io.ByteArrayOutputStream
import java.security.Permission

//  https://github.com/scallop/scallop/blob/develop/src/test/scala/CapturingTest.scala
trait Capturing {
  /** Captures all output from the *fn* block into two strings - (stdout, stderr). */
  def captureOutput(fn: => Unit):(String,String) = {
    val normalOut = Console.out
    val normalErr = Console.err
    val streamOut = new ByteArrayOutputStream()
    val streamErr = new ByteArrayOutputStream()
    Console.withOut(streamOut) {
      Console.withErr(streamErr) {
        fn
      }
    }
    (streamOut.toString, streamErr.toString)
  }

  /** Supresses exit in *fn* block. Returns list of exit statuses that were attempted. */
  def trapExit(fn: => Unit):List[Int] = {
    @volatile var statuses = List[Int]()
    val normalSM = System.getSecurityManager
    object SM extends SecurityManager {
      override def checkExit(status:Int) {
        statuses ::= status
        throw new SecurityException
      }
      override def checkPermission(p:Permission) {}
    }
    System.setSecurityManager(SM)
    try {
      fn
    } catch {
      case e:SecurityException =>
    }
    System.setSecurityManager(normalSM)
    statuses.reverse
  }

  /** Supresses exits in *fn* block, and captures stdout/stderr. */
  def captureOutputAndExits(fn: => Unit): (String, String, List[Int]) = {
    var exits = List[Int]()
    val (out, err) = captureOutput {
      exits = trapExit(fn)
    }
    (out, err, exits)
  }

}
