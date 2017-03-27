package com.ibm.sparktc.sparkbench.workload
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions.getOrDefault

case class WorkloadConfig (
                            name: String,
                            runs: Int,
                            parallel: Boolean,
                            inputDir: String,
                            workloadResultsOutputDir: Option[String],
                            outputDir: String,
                            workloadSpecific: Map[String, Any]
)
object WorkloadConfig {

  def fromMap(m: Map[String, Any]): WorkloadConfig = {

    // it's possible that I could use .getClass to get the names of the
    // member values of the case class and build this from that, but this is
    // easiest for now.
    def commonKeys: Seq[String] = Seq(      
      "name",
      "runs",
      "parallel",
      "inputdir",
      "workloadresultsoutputdir",
      "outputDir"
    )

    val workloadSpecific: Map[String, Any] = {
      val otherKeys: Seq[String] = m.keys.toSeq.diff(commonKeys)
      otherKeys.map( str => str -> m.get(str)).toMap
    }


    WorkloadConfig(
      getOrDefault(m, "name", ""),
      getOrDefault(m, "runs", 1),
      getOrDefault(m, "parallel", false),
      getOrDefault(m, "inputdir", ""),
      m.get("workloadresultsoutputdir") match {
        case Some(s) => Some(s.asInstanceOf[String])
        case _ => None
      },
      getOrDefault(m, "outputdir", ""),
      workloadSpecific
    )
  }

}
