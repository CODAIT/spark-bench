val scalacheckVersion = "1.12.5"
val junitVersion = "4.12"
val scalatestVersion = "3.0.1"

libraryDependencies ++= Seq(
  "org.rogach"                  %% "scallop"               % "2.1.1",
  "junit"                       % "junit"                  % junitVersion            % "test",
  "org.scalacheck"              %% "scalacheck"            % scalacheckVersion       % "test",
  "org.scalactic"               %% "scalactic"             % scalatestVersion        % "test",
  "org.scalatest"               %% "scalatest"             % scalatestVersion        % "test"
)