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

import org.scalatest.{FlatSpec, Matchers}

class GeneralFunctionsTest extends FlatSpec with Matchers {
  import GeneralFunctions._

  private def stoi(a: Any): Int = {
    a.toString.toInt
  }

  "GeneralFunctions" should "tryWithDefault with good input" in {
    tryWithDefault[Int]("8", 12, stoi) shouldBe 8 //scalastyle:ignore
  }

  it should "tryWithDefault with bad input" in {
    tryWithDefault[Int]("f", 8, stoi) shouldBe 8 //scalastyle:ignore
  }

  it should "test any2Long with good input" in {
    any2Long(8L) shouldBe 8L //scalastyle:ignore
  }

  it should "test any2Long with string input" in {
    any2Long("8") shouldBe 8L //scalastyle:ignore
  }

  it should "test any2Long with bad input" in {
    a[java.lang.NumberFormatException] should be thrownBy any2Long("f")
  }

  it should "getOrDefaultOpt with good input" in {
    val txt = "this is a great test"
    getOrDefaultOpt[String](Some(txt), "nope") shouldBe txt
  }

  it should "getOrDefaultOpt with no input" in {
    getOrDefaultOpt[String](None, "nope") shouldBe "nope"
  }

  it should "getOrDefaultOpt with good input and a custom fn" in {
    getOrDefaultOpt[Int](Some("8"), 12, stoi) shouldBe 8 //scalastyle:ignore
  }

  it should "time stuff" in {
    val (d, _) = time(Thread.sleep(50)) //scalastyle:ignore
    d shouldBe 57000000L +- 10000000L
  }

  it should "getOrThrow with good input" in {
    getOrThrow[String](Some("hi")) shouldBe "hi"
  }

  it should "getOrThrow with bad input" in {
    an[Exception] should be thrownBy getOrThrow[String](None)
  }

  it should "getOrThrow with good input in the map" in {
    val foo = getOrThrow(Map("foo" -> 8), "foo")
    foo shouldBe an[Any]
    val exp: Any = 8
    foo shouldBe exp
  }

  it should "getOrThrow when missing keys" in {
    an[Exception] should be thrownBy getOrThrow(Map("foo" -> 8), "bar")
  }

  it should "getOrThrowT with good input in the map" in {
    getOrThrowT[Int](Map("foo" -> 8), "foo") shouldBe 8
  }

  it should "getOrThrowT with bad input in the map" in {
    an[Exception] should be thrownBy getOrThrowT[Int](Map("foo" -> "8"), "foo")
  }

  it should "getOrThrowT with missing keys" in {
    an[Exception] should be thrownBy getOrThrowT[Int](Map("foo" -> 8), "bar")
  }

  it should "optionallyGet with valid input" in {
    optionallyGet[Int](Map("foo" -> 8), "foo") shouldBe Some(8) //scalastyle:ignore
  }

  it should "optionallyGet with missing input" in {
    optionallyGet[Int](Map("foo" -> 8), "bar") shouldBe empty
  }

  it should "optionallyGet with invalid input" in {
    optionallyGet[Int](Map("foo" -> "8"), "bar") shouldBe empty
  }

  it should "stringifyStackTraces" in {
    val e = SparkBenchException("this is an exception")
    stringifyStackTrace(e) should startWith ("com.ibm.sparktc.sparkbench.utils.SparkBenchException: this is an exception")
  }
}
