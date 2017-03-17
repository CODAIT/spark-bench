scalaVersion := "2.11.8"

val scalacheckVersion = "1.11.4"
val junitVersion = "4.12"
val scalatestVersion = "2.2.4"

libraryDependencies ++= Seq(
  "org.rogach"                  %% "scallop"               % "2.1.1",
  "junit"                       % "junit"                  % junitVersion            % "test",
  "org.scalacheck"              %% "scalacheck"            % scalacheckVersion       % "test",
  "org.scalactic"               %% "scalactic"             % scalatestVersion        % "test",
  "org.scalatest"               %% "scalatest"             % scalatestVersion        % "test"
)