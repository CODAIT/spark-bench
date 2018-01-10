/**
  * (C) Copyright IBM Corp. 2015 - 2017
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */

package com.ibm.sparktc.sparkbench.sparklaunch.submission.livy

import com.ibm.sparktc.sparkbench.sparklaunch.confparse.SparkJobConf
import com.ibm.sparktc.sparkbench.sparklaunch.submission.livy.LivySubmit._
import com.ibm.sparktc.sparkbench.sparklaunch.submission.Submitter
import com.ibm.sparktc.sparkbench.utils.SparkBenchException
import com.softwaremill.sttp.{Id, SttpBackend}
import org.slf4j.{Logger, LoggerFactory}

object LivySubmit {
  val log: Logger = LoggerFactory.getLogger(this.getClass)
  val successCode = 200

  import com.softwaremill.sttp._

  val emptyBodyException: SparkBenchException = SparkBenchException("REST call returned empty message body")
  val nonSuccessCodeException: Int => SparkBenchException = (code: Int) => SparkBenchException(s"REST call returned non-sucess code: $code")



  def apply(): LivySubmit = {
    new LivySubmit()(HttpURLConnectionBackend())
  }

  def cancelAllBatches(livyWithID: LivyRequestWithID)(implicit backend: SttpBackend[Id, Nothing]): Response[ResponseBodyDelete] = {
    log.info(s"Cancelling batch request id: ${livyWithID.id}")
    val response = livyWithID.deleteRequest.send()
    (response.is200, response.body) match {
      case (true, Right(bod)) => if (bod.msg == "deleted") response else throw SparkBenchException(s"Unexpected status for delete request: ${bod.msg}")
      case (true, Left(b))    => throw emptyBodyException
      case (_, _)             => throw nonSuccessCodeException(response.code)
    }
  }

  def sendPostBatchRequest(conf: SparkJobConf)
                          (implicit backend: SttpBackend[Id, Nothing]):
                            (LivyRequestWithID, Response[ResponseBodyBatch]) = {
    val livyRequest = LivyRequest(conf)
    println(s"Sending Livy POST request:\n${livyRequest.postRequest.toString}")
    val response: Id[Response[ResponseBodyBatch]] = livyRequest.postRequest.send()
    (response.isSuccess, response.body) match {
      case (true, Left(_)) => throw emptyBodyException
      case (false, Left(_)) => throw nonSuccessCodeException(response.code)
      case (false, Right(bod)) => throw SparkBenchException(s"POST Request to ${livyRequest.postBatchUrl} failed:\n" +
        s"${bod.log.mkString("\n")}")
      case (_,_) => // no exception thrown
    }
    val livyWithID = LivyRequestWithID(livyRequest, response.body.right.get.id)
    (livyWithID, response)
  }

  private def pollHelper(request: LivyRequestWithID, sleeperThread: Thread)(implicit backend: SttpBackend[Id, Nothing]): Response[ResponseBodyState] = {
    sleeperThread.run()
    log.info(s"Sending Livy status GET request:\n${request.statusRequest.toString}")
    val response: Id[Response[ResponseBodyState]] = request.statusRequest.send()
    response
  }

  def poll(request: LivyRequestWithID, sleeperThread: Thread, response: Response[ResponseBodyState])
          (implicit backend: SttpBackend[Id, Nothing]): Response[ResponseBodyState] = (response.isSuccess, response.body) match {
    case (false, _) => throw SparkBenchException(s"Request failed with code ${response.code}")
    case (_, Left(_)) => throw emptyBodyException
    case (true, Right(bod)) => bod.state match {
      case "success" => response
      case "dead" => throw SparkBenchException(s"Poll request failed with state: dead\n" + getLogs(request))
      case "running" => poll(request, sleeperThread, pollHelper(request, sleeperThread))
      case st => throw SparkBenchException(s"Poll request failed with state: $st")
    }
  }

  def getLogs(request: LivyRequestWithID)(implicit backend: SttpBackend[Id, Nothing]): String = {
    val response = request.logRequest.send()
    (response.is200, response.body) match {
      case (true, Right(bod)) => bod.log.mkString("\n")
      case (false, Right(_)) => throw SparkBenchException(s"Log request failed with code: ${response.code}")
      case (_, Left(_)) => throw emptyBodyException
    }
  }
}

class LivySubmit()(implicit val backend: SttpBackend[Id, Nothing]) extends Submitter {
  override def launch(conf: SparkJobConf): Unit = {
    val (livyWithID, postResponse) = sendPostBatchRequest(conf)(backend)
    val sleeperThread = new Thread{
      override def run(): Unit = Thread.sleep(livyWithID.pollSeconds * 1000)
    }
    val pollResponse = poll(livyWithID, sleeperThread, pollHelper(livyWithID, sleeperThread))(backend)
    sys.ShutdownHookThread {
      // cancel any polling
      sleeperThread.interrupt()
      // interrupt any batches
      cancelAllBatches(livyWithID)(backend)
    }
  }
}
