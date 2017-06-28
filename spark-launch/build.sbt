parallelExecution in Test := false

val horrible = TaskKey[Unit]("horrible", "why")
horrible := {
  "echo This is horrible..................................................................................".!
  "sbt cli/assembly".!
  "echo Okay I'm done being horrible......................................................................".!
}

//test in Test <<= test in Test dependsOn (Keys.assembly in Compile)
