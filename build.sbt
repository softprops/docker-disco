organization := "me.lessis"

name := "docker-disco"

version := "0.1.0-SNAPSHOT"

crossScalaVersions := Seq("2.10.4", "2.11.2")

scalaVersion := crossScalaVersions.value.head

resolvers += "softprops-maven" at "http://dl.bintray.com/content/softprops/maven"

libraryDependencies ++= Seq(
  "me.lessis" %% "docker-watch" % "0.1.0-SNAPSHOT",
  "me.lessis" %% "zoey-core" % "0.1.0"
)
