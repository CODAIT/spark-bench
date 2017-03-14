package com.ibm.sparktc.sparkbench
import java.io.File

import com.ibm.sparktc.sparkbench.datagen.DataGenerationConf
import com.ibm.sparktc.sparkbench.datagen.mlgenerator.KmeansDataGen
import org.scalatest._

import scala.io.Source

class KMeansDataGenTest extends FlatSpec with Matchers with BeforeAndAfter {

  val fileName = s"/tmp/kmeans/${java.util.UUID.randomUUID.toString}.csv"

  var file: File = _

  before {
    file = new File(fileName)
  }

  after {
    if(file.exists()) file.delete()
  }

  "KMeansDataGeneration" should "generate data correctly" in {
    val x = DataGenerationConf(
      generatorName = "kmeans",
      numRows = 10,
      outputFormat = "csv",
      outputDir = fileName,
      generatorSpecific = Map.empty
    )

    val generator = new KmeansDataGen(x)

    generator.run()


    val fileList = file.listFiles().toList.filter(_.getName.startsWith("part"))

    val fileContents: List[String] = fileList
      .flatMap(
        Source.fromFile(_)
          .getLines()
          .toList
      )

    fileContents.length shouldBe x.numRows

  }
/* IDK why IntelliJ is whining about wanting this implemented. Uncommenting this soothes IntelliJ but it blows up in SBT test. */
//  override protected def withFixture(test: Any): Outcome = {super.withFixture(test)}
}