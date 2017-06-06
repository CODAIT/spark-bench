import Dependencies._

/*
    ****************************************************************
    * Accessory Methods (have to be up here for forward reference) *
    ****************************************************************
*/

val sparkBenchJar = settingKey[String]("jar name and relative path for spark-bench")
val sparkBenchLaunchJar = settingKey[String]("jar name and relative path for spark-bench-launch")
val assemblyFile = settingKey[String]("folder where assembled jars go")
val sparklaunchTestResourcesJarsFile = settingKey[String]("folder where compiled jar goes")

lazy val commonSettings = Seq(
  organization := "com.ibm.sparktc",
  scalaVersion := "2.11.8",
  parallelExecution in Test := false,
  test in assembly := {},
  sparkBenchJar := s"spark-bench-${version.value}.jar",
  sparkBenchLaunchJar := s"spark-bench-launch-${version.value}.jar",
  assemblyFile := s"${baseDirectory.value.getParent}/target/assembly",
  sparklaunchTestResourcesJarsFile := s"${baseDirectory.value.getPath}/src/test/resources/jars/",
  testOptions in Test += Tests.Argument("-oF")
)

/*
    ***************************
    *       PROJECT           *
    ***************************
*/

lazy val utils = project
  .settings(
    commonSettings,
    libraryDependencies ++= sparkDeps,
    libraryDependencies ++= testDeps
  )

lazy val workloads = project
  .settings(
    commonSettings,
    libraryDependencies ++= sparkDeps,
    libraryDependencies ++= otherCompileDeps,
    libraryDependencies ++= testDeps
  )
  .dependsOn(utils % "compile->compile;test->test")

lazy val datageneration = project
  .settings(
    commonSettings,
    libraryDependencies ++= sparkDeps,
    libraryDependencies ++= otherCompileDeps,
    libraryDependencies ++= testDeps
  )
  .dependsOn(utils % "compile->compile;test->test")

val cleanJars = TaskKey[Unit]("cleanJars", "remove jars before assembling jar for spark-launch test")

lazy val cli = project
  .settings(
    commonSettings,
    name := "spark-bench",
    cleanJars := {
      println("\tCleaning up jars before assembling")
      s"rm ${assemblyFile.value}/*.jar".!
      s"rm ./spark-launch/src/test/resources/jars/*.jar".!
      println("\tDone")
    },
    assembly in Compile := {
      ((assembly in Compile) dependsOn(cleanJars in Test)).value
    },
    mainClass in assembly := Some("com.ibm.sparktc.sparkbench.cli.CLIKickoff"),
    assemblyOutputPath in assembly := new File(s"${assemblyFile.value}/${sparkBenchJar.value}"),
    libraryDependencies ++= sparkDeps,
    libraryDependencies ++= testDeps,
    libraryDependencies ++= typesafe,
    mainClass in assembly := Some("com.ibm.sparktc.sparkbench.cli.CLIKickoff")

  )
  .dependsOn(workloads, datageneration, utils % "compile->compile;test->test")
  .aggregate(utils, workloads, datageneration)

val moveJar = TaskKey[Unit]("moveJars", "move the assembled jars for spark-launch test")

lazy val `spark-launch` = project
  .settings(

    moveJar in Test := {
      (assembly in Compile in cli).value
      println("\tMoving assembled jar to resources folder for test")
      s"cp ${assemblyFile.value}/${sparkBenchJar.value} ${sparklaunchTestResourcesJarsFile.value}".!
      println("\tDone")
    },
    test in Test := {
      ((test in Test) dependsOn(moveJar in Test)).value
    },
    commonSettings,
    name := "spark-bench-launch",
    mainClass in assembly := Some("com.ibm.sparktc.sparkbench.sparklaunch.SparkLaunch"),
    assemblyOutputPath in assembly := new File(s"${assemblyFile.value}/${sparkBenchLaunchJar.value}"),
    libraryDependencies ++= sparkDeps,
    libraryDependencies ++= testDeps,
    libraryDependencies ++= typesafe
  )
  .dependsOn(utils % "compile->compile;test->test")


/*
    *************************
    *       TASKS           *
    *************************
*/

val dist = TaskKey[Unit]("dist", "Makes the distribution file for release")
dist := {
  val dir = baseDirectory.value.getName
  val parent = baseDirectory.value.getParent

  val tmpFolder = s"./${name.value}_${version.value}"

  s"rm -rf $tmpFolder"

  s"echo Making tmpFolder".!
  s"mkdir $tmpFolder".!
  s"mkdir $tmpFolder/lib".!

  s"cp readme.md $tmpFolder".!
  s"cp -r bin $tmpFolder/".!
  s"cp target/assembly/*.jar $tmpFolder/lib".!
  s"cp kmeans-example.sh $tmpFolder".!
  s"cp multi-submit-example.sh $tmpFolder".!
  s"cp multi-submit-sleep.conf $tmpFolder".!

  val buildNum = sys.env.get("TRAVIS_BUILD_NUMBER")
  val artifactName = buildNum match {
    case None => s"${name.value}_${version.value}.tgz"
    case Some(bn) => s"${name.value}_${version.value}_$bn.tgz"
  }

  s"tar -zcf ./$artifactName $tmpFolder".!

}

val rmDist = TaskKey[Unit]("rmDist", "removes all the dist files")
rmDist := {
  val dir = baseDirectory.value.getName
  val parent = baseDirectory.value.getParent

  val tmpFolder = s"./${name.value}_${version.value}"
  s"echo Removing $tmpFolder".!
  s"rm -rf $tmpFolder".!
  s"echo Removing $tmpFolder.tgz".!
  s"""rm -f spark-bench*.tgz""".!
  s"echo rmDist Complete!".!

}

dist := (dist dependsOn rmDist).value
dist := (dist dependsOn assembly).value

clean := (clean dependsOn rmDist).value

