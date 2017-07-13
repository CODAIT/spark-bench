import Dependencies._
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption.REPLACE_EXISTING

/*
    **********************************************************************************
    * Common Settings and val Definitions (have to be up here for forward reference) *
    **********************************************************************************
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
    *       PROJECTS          *
    ***************************
*/

lazy val utils = project
  .settings(
    commonSettings,
    libraryDependencies ++= sparkDeps,
    libraryDependencies ++= testDeps
  )

/*
    There's some extra code here to clean up any created jars before assembly.
    Assembly is called by regular sbt assembly and by sbt spark-launch/test.
    See note below about why spark-launch/test calls cli/assembly.
 */
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
    libraryDependencies ++= otherCompileDeps,
    libraryDependencies ++= testDeps,
    libraryDependencies ++= typesafe
  )
  .dependsOn(utils % "compile->compile;test->test")
  .aggregate(utils)

lazy val `test-workloads` = project
  .settings(
    commonSettings,
    name := "test-workloads",
    libraryDependencies ++= sparkDeps,
    libraryDependencies ++= testDeps
  )
  .dependsOn(utils % "compile->compile;test->test", cli % "compile->compile")

/*
    spark-launch relies on having the cli uber jar to launch through spark-submit. So
    in order to test spark-launch, we have to assemble the cli jar and move it into a folder
    that's accessible to the test code for spark-launch.
 */
val moveJar = TaskKey[Unit]("moveJars", "move the assembled jars for spark-launch test")

lazy val `spark-launch` = project
  .settings(

    moveJar in Test := {
      val log = streams.value.log
      log.info("Assembling spark-bench and custom-workload JARs...")
      (assembly in Compile in cli).value // This is the magic sauce
      log.info("Moving assembled JARs to resources folder for test")
      s"cp ${assemblyFile.value}/${sparkBenchJar.value} ${sparklaunchTestResourcesJarsFile.value}".!
      val customTestJar = (Keys.`package` in Compile in `test-workloads`).value
      s"cp $customTestJar ${sparklaunchTestResourcesJarsFile.value}".!
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
  .dependsOn(utils % "compile->compile;test->test", cli % "compile->compile;test->test")

/*
    *******************************
    *      CUSTOM TASKS           *
    *******************************
*/

val dist = TaskKey[Unit]("dist", "Makes the distribution file for release")
dist := {
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

  // MAKE SURE YOU DON'T PUT TRAILING SLASHES ON THESE FILES!! It changes behavior between GNU cp and BSD cp
  val binFolder = s"${baseDirectory.value.getPath}/bin"
  s"cp -r $binFolder $tmpFolder".!
  log.info("...copying contents of target/assembly/")

  // Reverting to java API here because cp works differently between the GNU and BSD versions. >:(
  val folder = new File(s"${baseDirectory.value.getPath}/target/assembly")
  val files = folder.listFiles()
  println(files.foreach(f => println(f.getPath)))
  files.map( fyle =>
    Files.copy(
      fyle.toPath,
      new File(s"${baseDirectory.value.getPath}/$tmpFolder/lib/${fyle.toPath.getFileName}").toPath,
      REPLACE_EXISTING))

  log.info("...copying examples/")

  // MAKE SURE YOU DON'T PUT TRAILING SLASHES ON THESE FILES!! It changes behavior between GNU cp and BSD cp
  val examplesFolder = s"${baseDirectory.value.getPath}/examples"
  s"cp -r $examplesFolder $tmpFolder".!

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
  s"""rm -f $tmpFolder.tgz""".!
  log.info("Distribution files removed.")
}

val rmTemp = TaskKey[Unit]("rmTemp", "removes temporary testing files")
rmTemp := {
  val tmpFolder = "/tmp/spark-bench-scalatest"
  streams.value.log.info(s"Removing $tmpFolder...")
  s"rm -rf $tmpFolder".!
}

dist := (dist dependsOn assembly).value

clean := (clean dependsOn rmDist dependsOn rmTemp).value
