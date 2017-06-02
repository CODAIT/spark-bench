import sbt.Keys.version

object Constants {
  val sparkBenchJar = s"target/assembly/spark-bench-${version}.jar"
  val sparkBenchLaunchJar = s"target/assembly/spark-bench-launch-${version}.jar"
}