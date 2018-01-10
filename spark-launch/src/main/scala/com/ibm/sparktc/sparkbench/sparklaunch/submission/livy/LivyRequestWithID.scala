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

import com.softwaremill.sttp.json4s.asJson
import com.softwaremill.sttp._

case class LivyRequestWithID(
                              requestBody: LivyRequestBody,
                              url: String,
                              pollSeconds: Int,
                              id: Int
                            ) {
  private val getStatusUrl = uri"$url/batches/$id/state"
  private val getLogUrl = uri"$url/batches/$id/log"
  private val deleteBatchUrl = uri"$url/batches/$id"

  val statusRequest: RequestT[Id, ResponseBodyState, Nothing] =
    sttp
      .get(getStatusUrl)
      .contentType("application/json")
      .response(asJson[ResponseBodyState])

  val logRequest: RequestT[Id, ResponseBodyLog, Nothing] =
    sttp
      .get(getLogUrl)
      .contentType("application/json")
      .response(asJson[ResponseBodyLog])

  val deleteRequest: Request[ResponseBodyDelete, Nothing] =
    sttp
      .delete(deleteBatchUrl)
      .contentType("application/json")
      .response(asJson[ResponseBodyDelete])

}

object LivyRequestWithID {
  def apply(livyRequest: LivyRequest, id: Int): LivyRequestWithID = {
    LivyRequestWithID(
      livyRequest.requestBody,
      livyRequest.url,
      livyRequest.pollSeconds,
      id
    )

  }
}
