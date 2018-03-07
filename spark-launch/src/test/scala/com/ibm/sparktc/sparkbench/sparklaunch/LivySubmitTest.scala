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

package com.ibm.sparktc.sparkbench.sparklaunch

import com.ibm.sparktc.sparkbench.sparklaunch.submission.livy.LivySubmit
import com.ibm.sparktc.sparkbench.sparklaunch.confparse.SparkJobConf
import com.softwaremill.sttp.{HttpURLConnectionBackend, Method, Request, Response, StringBody}
import com.softwaremill.sttp.testing.SttpBackendStub
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

class LivySubmitTest extends FlatSpec with Matchers with BeforeAndAfterEach {

  val conf: String =
    """
      |spark-bench = {
        |spark-submit-config = [{
          |livy = {
          |  url = "localhost:8998"
          |  poll-seconds = 1
          |}
          |workload-suites = [
          |{
          |  descr = "One run of SparkPi and that's it!"
          |  benchmark-output = "console"
          |  workloads = [
          |    {
          |      name = "sparkpi"
          |      slices = 10
          |    }
          |  ]
          |}]
        |}]
      |}
    """.stripMargin

  var pollCounter = 0
  val howManyTimesToPoll = 2
  val batchID: Int = scala.math.abs(scala.util.Random.nextInt())

  def mockJobIsDone(): Boolean = {
    if (pollCounter < howManyTimesToPoll) {pollCounter += 1; false }
    else true
  }

  "Simple LivyRequest" should "return a success code" in {

    val sparkSubmitScriptConf = SparkJobConf(
      className = "com.ibm.sparktc.sparkbench.cli.CLIKickoff",
      sparkBenchJar = SparkJobConf.getSparkBenchJar(ConfigFactory.parseString("{}")),
      sparkConfs =  Map.empty[String, String],
      sparkArgs =  Map.empty[String, String],
      childArgs = Seq(conf),
      submissionParams = Map("url" -> "localhost:8998", "poll-seconds" -> 1)
    )
    val expectedBody =
      s"""{"file":"file://${sparkSubmitScriptConf.sparkBenchJar}",""" +
        """"className":"com.ibm.sparktc.sparkbench.cli.CLIKickoff","args":["\nspark-bench = {\nspark-submit-config = [{\nlivy = {\n  url = \\\"localhost:8998\\\"\n  poll-seconds = 1\n}\nworkload-suites = [\n{\n  descr = \\\"One run of SparkPi and that's it!\\\"\n  benchmark-output = \\\"console\\\"\n  workloads = [\n    {\n      name = \\\"sparkpi\\\"\n      slices = 10\n    }\n  ]\n}]\n}]\n}\n    "]}""".stripMargin

    val jobAcceptedCode = 201

    //val add: (Int, Int) => Int = (x,y) => { x + y }


    def postRequest(request: Request[_,_], expectedBody: String): Boolean = {
      request.method == Method.POST &&
      request.uri.path.startsWith(List("batches")) &&
      request.headers.contains(("Content-Type", "application/json")) &&
      request.body.leftSideValue == StringBody(expectedBody, "utf-8", Some("application/json"))
    }

    val postResponse = (id: Int) => Response(
      Right(
        s"""{    "id": $id,    "state": "running",    "appId": null,    "appInfo": {        """ +
        """"driverLogUrl": null,        "sparkUiUrl": null    },    "log": [        "stdout: ",        "\stderr: "    ]}"""
      ),
      code = jobAcceptedCode,
      statusText = "",
      Nil,
      Nil
    )

    def pollRequest(request: Request[_,_]): Boolean = {
      request.uri.path.head == "batches" &&
      (if(request.uri.path.length > 1) request.uri.path.tail.head.toInt == batchID else false) &&
      request.method == Method.GET
    }

    val pollResponse = (id: Int, done: Boolean) => {
      val state = done match {
        case true => "success"
        case false => "running"
      }
      Response(
        Right(s"""{"id":$id,"state":"$state"}""".stripMargin),
        code = 200, // scalastyle:ignore
        statusText = "",
        Nil,
        Nil
      )
    }

    def deleteRequest(request: Request[_,_]): Boolean = {
      request.uri.path.head == "batches" &&
        (if(request.uri.path.length > 1) request.uri.path.tail.head.toInt == batchID else false) &&
        request.method == Method.DELETE
    }

    val deleteResponse = Response(
      Right(
        s"""{"msg":"deleted"}"""
      ),
      code = 200, // scalastyle:ignore
      statusText = "",
      Nil,
      Nil
    )

    val testingBackend = SttpBackendStub(HttpURLConnectionBackend())
      .whenRequestMatchesPartial({
        case r if pollRequest(r) => pollResponse(batchID, mockJobIsDone())
        case r if postRequest(r, expectedBody) => postResponse(batchID)
        case r if deleteRequest(r) => deleteResponse
      })

    val submitter = new LivySubmit()(testingBackend)

    submitter.launch(sparkSubmitScriptConf)
  }

  ignore should "work when there's a server locally that it can hit" in {
    val relativePath = "/etc/local-livy-example.conf"
    val resource = getClass.getResource(relativePath)
    val path = resource.getPath
    SparkLaunch.main(Array(path))
  }



}
