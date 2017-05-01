package com.ibm.sparktc.sparkbench.cli

import com.ibm.sparktc.sparkbench.workload.Suite

case class SparkContextConf(
                           //todo all the exec mem, cores, etc. will go here
                            suites: Seq[Suite]
                           ) {

}
