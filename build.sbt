name := """pod"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= {
  val akkaV = "2.3.6"
  val sprayV = "1.3.2"
  Seq(
    jdbc,
    anorm,
    cache,
    ws,
    "org.mongodb" %% "casbah" % "2.8.0",
    "org.scalaj" %% "scalaj-http" % "1.1.3",
    "net.liftweb" %% "lift-json" % "2.6+",
    "org.json4s" %% "json4s-jackson" % "3.2.11",
    "org.json4s" %% "json4s-native" % "3.2.11",
    "io.spray"            %%  "spray-can"     % "1.3.2",
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-json"    % "1.3.1",
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.specs2"          %%  "specs2-core"   % "2.3.11" % "test",
    "org.scalaz"          %%  "scalaz-core"   % "7.1.0"
  )
}
