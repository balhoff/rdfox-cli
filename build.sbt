enablePlugins(JavaAppPackaging)

organization  := "org.rti.bioinformatics"

name          := "rdfox-cli"

version       := "0.0.3-SNAPSHOT"

scalaVersion  := "2.11.8"

mainClass in Compile := Some("org.rti.bioinformatics.rdfox.Main")

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

scalacOptions in Test ++= Seq("-Yrangepos")

resolvers += Resolver.mavenLocal

javaOptions += "-Xmx16G"

packageName in Universal := s"${name.value}"

// Download JRDFox.jar for your platform
// from https://www.cs.ox.ac.uk/isg/tools/RDFox/otherOS_download.html
// Place in `lib`

libraryDependencies ++= {
  Seq(
    "org.backuity.clist"          %% "clist-core"            % "3.2.1",
    "org.backuity.clist"          %% "clist-macros"          % "3.2.1" % "provided",
    "net.sourceforge.owlapi"      %  "owlapi-distribution"   % "3.5.4",
    "org.apache.directory.studio" %  "org.apache.commons.io" % "2.4",
    "org.scalaz"                  %% "scalaz-core"           % "7.2.1",
    "com.typesafe.scala-logging"  %% "scala-logging"         % "3.4.0",
    "ch.qos.logback"              %  "logback-classic"       % "1.1.7",
    "org.codehaus.groovy"         %  "groovy-all"            % "2.4.6",
    "org.openrdf.sesame"          %  "sesame-model"          % "2.7.16",
    "org.openrdf.sesame"          %  "sesame-sail"           % "2.7.16",
    "org.openrdf.sesame"          %  "sesame-rio-api"        % "2.7.16",
    "org.openrdf.sesame"          %  "sesame-rio-turtle"     % "2.7.16"
  )
}
