name := "StronglyConnectedComponentApp" 

version := "1.0"

scalaVersion := "2.10.4"

libraryDependencies += "org.apache.spark" %% "spark-core" % "1.3.0"

libraryDependencies += "org.apache.spark" % "spark-mllib_2.10" % "1.3.0"

libraryDependencies += "org.apache.spark" % "spark-graphx_2.10" % "1.3.0"

libraryDependencies += "org.apache.hadoop" % "hadoop-client" % "2.4.0"

resolvers +="Local Maven Repository" at "file:///home/limin/.m2/repository"

resolvers +=Resolver.mavenLocal

resolvers += "Akka Repository" at "http://repo.akka.io/releases/"
