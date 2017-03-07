name := "sparkbench"
organization := "com.ibm.sparktc"

lazy val core = project
lazy val workloads = project
lazy val cli = project.dependsOn(core)
