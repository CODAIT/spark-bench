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
import com.ibm.sparktc.sparkbench.utils.SparkBenchException
import com.softwaremill.sttp.json4s.asJson
import com.softwaremill.sttp.json4s._
import com.softwaremill.sttp._

case class LivyRequest(
                        requestBody: LivyRequestBody,
                        url: String,
                        pollSeconds: Int
                      ) {
  val postBatchUrl = uri"$url/batches"

  val postRequest: RequestT[Id, ResponseBodyBatch, Nothing] =
    sttp
      .body(requestBody)
      .contentType("application/json")
      .post(postBatchUrl)
      .response(asJson[ResponseBodyBatch])
}

object LivyRequest {
  def apply(conf: SparkJobConf): LivyRequest = {
    /* Drop any trailing slashes here for the sake of congruency */
    val url = {
      val str = conf.submissionParams("url").toString
      if(str.endsWith("/")) str.dropRight(1) else str
    }

    val pollSeconds = {
      val int = conf.submissionParams("poll-seconds").asInstanceOf[Int]
      if(int <= 0) throw SparkBenchException("poll-seconds cannot be less than or equal to 0 seconds. " +
        "Please modify your config file.")
      int
    }

    LivyRequest(
      requestBody = LivyRequestBody(conf),
      url,
      pollSeconds
    )
  }
}
