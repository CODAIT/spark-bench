import Dependencies._
import java.io.File
import java.nio.file.{Files, Path}
import java.nio.file.StandardCopyOption.REPLACE_EXISTING


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
      val log = streams.value.log
      log.info("Cleaning up jars before assembling")
      s"rm ${assemblyFile.value}/*.jar".!
      s"rm ./spark-launch/src/test/resources/jars/*.jar".!
      log.info("Done cleaning jars.")
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
      val log = streams.value.log
      log.info("Assembling spark-bench jar...")
      (assembly in Compile in cli).value
      log.info("Moving assembled spark-bench jar to resources folder for test")
      s"cp ${assemblyFile.value}/${sparkBenchJar.value} ${sparklaunchTestResourcesJarsFile.value}".!
      log.info("Done moving files.")
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

//  val x = (sparkBenchJar in Compile).value
//  val y = (sparkBenchLaunchJar in Compile).value

  val log = streams.value.log
  log.info("Creating distribution...")
  log.info("Assembling spark-bench jar...")
  dependsOn((assembly in Compile in cli).value)
  log.info("Assembling spark-bench-launch jar...")
  dependsOn((assembly in Compile in `spark-launch`).value)
  log.info("Done assembling jars")

  val dir = baseDirectory.value.getName
  val parent = baseDirectory.value.getParent
  val tmpFolder = s"./${name.value}_${version.value}"

  log.info(s"Creating folder $tmpFolder")
  s"mkdir $tmpFolder".!
  log.info(s"Creating folder $tmpFolder/lib")
  s"mkdir $tmpFolder/lib".!

  log.info("Copying files:")
  log.info("...copying README.md")
  s"cp readme.md $tmpFolder".!
  log.info("...copying bin/")
  s"cp -r bin $tmpFolder/".!
  log.info("...copying contents of target/assembly/")
  // this is so stupid. cp works differently in bash and bourne shell, thanks Apple

  val folder = new File(s"${baseDirectory.value.getPath}/target/assembly")
  val files = folder.listFiles()
  println(files.foreach(f => println(f.getPath)))
  files.map( fyle => Files.move(fyle.toPath, new File(s"${baseDirectory.value.getPath}/$tmpFolder/lib/${fyle.toPath.getFileName}").toPath, REPLACE_EXISTING))

//
//  s"cp target/assembly/$x $tmpFolder/lib/".!
//  s"cp target/assembly/$y $tmpFolder/lib/".!
  log.info("...copying examples")
  s"cp -r examples/ $tmpFolder".!
  log.info("Done copying files.")

  val buildNum = sys.env.get("TRAVIS_BUILD_NUMBER")
  val artifactName = buildNum match {
    case None => s"${name.value}_${version.value}.tgz"
    case Some(bn) => s"${name.value}_${version.value}_$bn.tgz"
  }

  log.info(s"Creating tar file: $artifactName")
  s"tar -zcf ./$artifactName $tmpFolder".!
  log.info("Done creating tar file")
  log.info(s"Distribution created: $artifactName")
}


val rmDist = TaskKey[Unit]("rmDist", "removes all the dist files")
rmDist := {
  val log = streams.value.log

  val dir = baseDirectory.value.getName
  val parent = baseDirectory.value.getParent

  val tmpFolder = s"./${name.value}_${version.value}"
  log.info(s"Removing $tmpFolder...")
  s"rm -rf $tmpFolder".!
  log.info(s"Removing $tmpFolder.tgz...")
  s"""rm -f spark-bench*.tgz""".!
  log.info("Distribution files removed.")
}


dist := (dist dependsOn assembly).value

clean := (clean dependsOn rmDist).value

