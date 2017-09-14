package com.ibm.sparktc.sparkbench.workload.exercise

import com.ibm.sparktc.sparkbench.testfixtures.SparkSessionProvider
import com.ibm.sparktc.sparkbench.utils.SparkBenchException
import org.scalatest.{FlatSpec, Matchers}

class SleepTest extends FlatSpec with Matchers {

  "The Sleep workload creation object" should "generate a case class properly with just sleepMS" in {

    val m: Map[String, Any] = Map(
      "name" -> "sleep",
      "sleepms" -> 6500
    )

    val thisResult: Sleep = Sleep(m)

    val that: Sleep = new Sleep(
      sleepMS = 6500L
    )

    thisResult.distribution shouldBe that.distribution
    thisResult.distributionMax shouldBe that.distributionMax
    thisResult.distributionMin shouldBe that.distributionMin
    thisResult.sleepMS shouldBe that.sleepMS
  }

  it should "throw an error when sleepMS and distribution are both specified" in {
    val m: Map[String, Any] = Map(
      "name" -> "sleep",
      "sleepms" -> 6500,
      "distribution" -> "poisson"
    )

    a [SparkBenchException] should be thrownBy Sleep(m)
  }

  it should "throw an error when neither sleepMS nor distribution are specified" in {
    val m: Map[String, Any] = Map(
      "name" -> "sleep",
      "mean" -> 11000
    )

    a [SparkBenchException] should be thrownBy Sleep(m)
  }

  it should "throw an error when sleepMS is negative" in {
    val m: Map[String, Any] = Map(
      "name" -> "sleep",
      "sleepms" -> -3
    )

    a [SparkBenchException] should be thrownBy Sleep(m)
  }

  it should "generate properly when uniform distribution is specified" in {
    val m = Map(
      "name" -> "sleep",
      "distribution" -> "uniform",
      "max" -> 6500,
      "min" -> 6498
    )

    val thisResult: Sleep = Sleep(m)

    val that: Sleep = new Sleep(
      distribution = Some("uniform"),
      distributionMax = Some(6500L),
      distributionMin = Some(6498L),
      sleepMS = -1 //just to satisfy constructor
    )

    thisResult.distribution shouldBe that.distribution
    thisResult.distributionMax shouldBe that.distributionMax
    thisResult.distributionMin shouldBe that.distributionMin
    thisResult.sleepMS should be >= that.distributionMin.get
    thisResult.sleepMS should be <= that.distributionMax.get
  }

  it should "generate properly when uniform distribution is specified and there is no min parameter present" in {
    val m = Map(
      "name" -> "sleep",
      "distribution" -> "uniform",
      "max" -> 6500
    )

    val thisResult: Sleep = Sleep(m)

    val that: Sleep = new Sleep(
      distribution = Some("uniform"),
      distributionMax = Some(6500L),
      distributionMin = Some(0L),
      sleepMS = -1 //just to satisfy constructor
    )

    thisResult.distribution shouldBe that.distribution
    thisResult.distributionMax shouldBe that.distributionMax
    thisResult.distributionMin shouldBe that.distributionMin
    thisResult.sleepMS should be >= 0L
    thisResult.sleepMS should be <= that.distributionMax.get
  }

  it should "throw an error when components needed for building with uniform distribution are not present" in {
    // No required "max" present
    val m = Map(
      "name" -> "sleep",
      "distribution" -> "uniform",
      "min" -> 6000
    )

    a [SparkBenchException] should be thrownBy Sleep(m)
  }



  it should "generate properly when Poisson distribution is specified" in {
    val m = Map(
      "name" -> "sleep",
      "distribution" -> "poisson",
      "mean" -> 10000.0
    )

    val thisResult: Sleep = Sleep(m)

    val that: Sleep = new Sleep(
      distribution = Some("poisson"),
      distributionMean = Some(10000.0),
      sleepMS = -1 //just to satisfy constructor
    )

    thisResult.distribution shouldBe that.distribution
    thisResult.distributionMean shouldBe that.distributionMean
    thisResult.sleepMS should be >= 0L
  }

  it should "throw an error when components needed for building with Poisson are not present" in {
    // No required mean present
    val m = Map(
      "name" -> "sleep",
      "distribution" -> "poisson"
    )

    a [SparkBenchException] should be thrownBy Sleep(m)
  }

  it should "generate properly when Gaussian distribution is specified" in {
    val m = Map(
      "name" -> "sleep",
      "distribution" -> "gaussian",
      "mean" -> 10000.0,
      "std" -> 1000.0
    )

    val thisResult: Sleep = Sleep(m)

    val that: Sleep = new Sleep(
      distribution = Some("gaussian"),
      distributionMean = Some(10000.0),
      distributionStd = Some(1000.0),
      sleepMS = -1 //just to satisfy constructor
    )

    thisResult.distribution shouldBe that.distribution
    thisResult.distributionMean shouldBe that.distributionMean
    thisResult.distributionStd shouldBe that.distributionStd
    thisResult.sleepMS should be >= 0L
  }

  it should "throw an error when components needed for building with Gaussian are not present" in {

    // Required mean not present
    val m1 = Map(
      "name" -> "sleep",
      "distribution" -> "gaussian",
      "std" -> 1000.0
    )

    a [SparkBenchException] should be thrownBy Sleep(m1)

    // Required standard deviation not present
    val m2 = Map(
      "name" -> "sleep",
      "distribution" -> "gaussian",
      "mean" -> 10000.0
    )

    a [SparkBenchException] should be thrownBy Sleep(m2)
  }

  "The Sleep workload" should "sleep for greater than or equal to the number of milliseconds specified" in {
    val m: Map[String, Any] = Map(
      "name" -> "sleep",
      "sleepms" -> 1000
    )

    val sleepWorkload: Sleep = Sleep(m)
    val oneRow = sleepWorkload.doWorkload(None, SparkSessionProvider.spark).head
    val runtime = oneRow.getAs[Long]("total_runtime")
    // Total runtime is measured in microseconds, so we first divide by 1,000,000
    val oneMillion = 1000000L

    /*
      There is overhead associated with the time() function itself,
      so the total time to run a thread sleep of x milliseconds will be x + some epsilon.
      Here we are using 50 milliseconds as a generous epsilon.
    */
    runtime / oneMillion shouldBe 1000L +- 50L
  }

}
