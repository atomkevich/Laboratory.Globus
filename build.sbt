name := """pod"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "org.mongodb" %% "casbah" % "2.8.0",
  "org.scalaj" %% "scalaj-http" % "1.1.3",
  "net.liftweb" %% "lift-json" % "2.6+",
  "org.json4s" %% "json4s-jackson" % "3.2.11",
  "org.json4s" %% "json4s-native" % "3.2.11"
)