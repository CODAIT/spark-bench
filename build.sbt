import Dependencies._
import Constants._

logLevel := Level.Warn

/*
    ****************************************************************
    * Accessory Methods (have to be up here for forward reference) *
    ****************************************************************
*/

def cleanJars(): Unit = {
  s"rm target/assembly/*.jar".!
  s"rm spark-launch/src/test/resources/jars/*.jar".!
}

//def assembleJarForTest(): File = {
//  val cool: File = (assembly in Compile in cli).value
//  cool
//}

def moveJar(): Unit = {
  s"mv target/assembly/*.jar spark-launch/src/test/resources/jars/".!
}

//def beforeTestInSparkLaunch(): Unit = {
//  println("\tCleaning Jars....")
//  cleanJars()
//  println("\tDone")
//  println("\tAssembling jar for test...")
//  assembleJarForTest()
//  println("\tDone")
//  println("\tMoving jar to resources file...")
//  moveJar()
//  println("\tDone")
//}

/*
    ***************************
    *       PROJECT           *
    ***************************
*/

lazy val commonSettings = Seq(
  organization := "com.ibm.sparktc",
  scalaVersion := "2.11.8",
  parallelExecution in Test := false,
  test in assembly := {}
)

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

lazy val cli = project
  .settings(
    commonSettings,
    name := "spark-bench",
    scalacOptions ++= Seq("-feature", "-Ylog-classpath"),
    mainClass in assembly := Some("com.ibm.sparktc.sparkbench.cli.CLIKickoff"),
//    assemblyJarName in assembly := s"spark-bench-${version.value}.jar",
    assemblyOutputPath in assembly := new File(sparkBenchJar),
    libraryDependencies ++= sparkDeps,
    libraryDependencies ++= testDeps,
    libraryDependencies ++= typesafe,
    mainClass in assembly := Some("com.ibm.sparktc.sparkbench.cli.CLIKickoff")

  )
  .dependsOn(workloads, datageneration, utils % "compile->compile;test->test")
  .aggregate(utils, workloads, datageneration)

val beforeSparkLaunchTest = TaskKey[Unit]("beforeSparkLaunchTest", "things to do before running tests in spark-launch")

lazy val `spark-launch` = project
  .settings(
    beforeSparkLaunchTest := {
      println("\tCleaning Jars....")
      cleanJars()
      println("\tDone")
      println("\tAssembling jar for test...")
      (assembly in Compile in cli)
      println("\tDone")
      println("\tMoving jar to resources file...")
      moveJar()
      println("\tDone")
    },
    test in Test := {
      ((test in Test) dependsOn(beforeSparkLaunchTest)).value
    },
    commonSettings,
    name := "spark-bench-launch",
    scalacOptions ++= Seq("-feature", "-Ylog-classpath"),
    mainClass in assembly := Some("com.ibm.sparktc.sparkbench.sparklaunch.SparkLaunch"),
//    assemblyJarName in assembly := s"spark-bench-launch-${version.value}.jar",
    assemblyOutputPath in assembly := new File(sparkBenchLaunchJar),
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


