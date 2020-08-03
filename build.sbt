name := "sensors-task"

version := "1.0"

scalaVersion := "2.13.1"

lazy val akkaVersion = "2.6.8"

fork := true

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
  "org.scalatest" %% "scalatest" % "3.1.0" % Test,
  "com.lightbend.akka" %% "akka-stream-alpakka-csv" % "2.0.1",
  "com.typesafe.akka" %% "akka-stream" % akkaVersion
)
