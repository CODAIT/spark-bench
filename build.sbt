import Dependencies._


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

lazy val root = (project in file("."))
  .settings(
    commonSettings,
    name := "spark-bench",
    scalacOptions ++= Seq("-feature", "-Ylog-classpath"),
    mainClass in assembly := Some("CLIKickoff"),
    assemblyJarName in assembly := s"spark-bench-${version.value}.jar"
  )
  .aggregate(utils, workloads, datageneration, cli)
  .dependsOn(utils, workloads, datageneration, cli)

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
  .dependsOn(utils)

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
    libraryDependencies ++= sparkDeps,
    libraryDependencies ++= testDeps,
    libraryDependencies ++= typesafe
  )
  .dependsOn(workloads, datageneration, utils)


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
  s"cp target/scala-2.11/spark-bench-${version.value}.jar $tmpFolder/lib".!
  s"cp kmeans-example.sh $tmpFolder".!

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
