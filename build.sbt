name := "logback-journal"

organization := "org.gnieh"

version := "0.1.0-SNAPSHOT"

licenses += ("The Apache Software License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

homepage := Some(url("https://github.com/gnieh/logback-journal"))

javaOptions += "-Djna.nosys=true"

libraryDependencies += "net.java.dev.jna" % "jna" % "4.1.0"

libraryDependencies += "ch.qos.logback"  % "logback-classic" % "1.1.1"

// OSGi settings
osgiSettings

resourceDirectories in Compile := List()

OsgiKeys.exportPackage := Seq("org.gnieh.logback")

OsgiKeys.additionalHeaders := Map (
  "Bundle-Name" -> "systemd journal appender for logback"
)

OsgiKeys.bundleSymbolicName := "org.gnieh.logback.journal"

OsgiKeys.privatePackage := Seq()

// publish settings
publishMavenStyle := true

publishArtifact in Test := false

// The Nexus repo we're publishing to.
publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
    if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots")
    else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

pomIncludeRepository := { x => false }

pomExtra :=
  <scm>
    <url>https://github.com/gnieh/logback-journal</url>
    <connection>scm:git:git://github.com/gnieh/logback-journal.git</connection>
    <developerConnection>scm:git:git@github.com:gnieh/logback-journal.git</developerConnection>
    <tag>HEAD</tag>
  </scm>
  <developers>
    <developer>
      <id>satabin</id>
      <name>Lucas Satabin</name>
      <email>lucas.satabin@gnieh.org</email>
    </developer>
  </developers>
  <issueManagement>
    <system>github</system>
    <url>https://github.com/gnieh/logback-journal/issues</url>
  </issueManagement>

