enablePlugins(JavaAppPackaging)

organization  := "org.rti.bioinformatics"

name          := "rdfox-cli"

version       := "0.0.1"

scalaVersion  := "2.11.8"

mainClass in Compile := Some("org.rti.bioinformatics.rdfox.Main")

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

scalacOptions in Test ++= Seq("-Yrangepos")

resolvers += Resolver.mavenLocal

javaOptions += "-Xmx12G"

// Download JRDFox.jar and the associated native libraries for your platform
// (e.g. libCppRDFox.*, libCppRDFox-logAPI.*)
// from https://www.cs.ox.ac.uk/isg/tools/RDFox/otherOS_download.html
// Place these in `lib`

libraryDependencies ++= {
  Seq(
    "org.backuity.clist"          %% "clist-core"            % "3.2.1",
    "org.backuity.clist"          %% "clist-macros"          % "3.2.1" % "provided",
    "net.sourceforge.owlapi"      %  "owlapi-distribution"   % "3.5.4",
    "org.apache.directory.studio" %  "org.apache.commons.io" % "2.4",
    "org.scalaz"                  %% "scalaz-core"           % "7.2.1",
    "com.typesafe.scala-logging"  %% "scala-logging"         % "3.4.0",
    "ch.qos.logback"              %  "logback-classic"       % "1.1.7",
    "org.codehaus.groovy"         %  "groovy-all"            % "2.4.6"
  )
}
