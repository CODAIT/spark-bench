resolvers += Resolver.url("bintray-sbt-plugins", url("http://dl.bintray.com/sbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns)

addSbtPlugin("com.eed3si9n" %% "sbt-assembly" % "0.14.3")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.0")
