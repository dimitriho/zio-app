name := "real-world-app"

version := "0.1"

scalaVersion := "2.13.1"

val Http4sVersion  = "0.21.0-M5"
val CirceVersion   = "0.12.3"
val LogbackVersion = "1.2.3"

libraryDependencies += "dev.zio" %% "zio"              % "1.0.0-RC16"
libraryDependencies += "dev.zio" %% "zio-interop-cats" % "2.0.0.0-RC7"

libraryDependencies ++= Seq(
  "org.http4s"     %% "http4s-blaze-server" % Http4sVersion,
  "org.http4s"     %% "http4s-blaze-client" % Http4sVersion,
  "org.http4s"     %% "http4s-circe"        % Http4sVersion,
  "org.http4s"     %% "http4s-dsl"          % Http4sVersion,
  "io.circe"       %% "circe-generic"       % CirceVersion,
  "ch.qos.logback" % "logback-classic"      % LogbackVersion,
)

libraryDependencies += "com.github.pureconfig" %% "pureconfig" % "0.12.1"

libraryDependencies += "org.tpolecat"               %% "doobie-core"   % "0.8.4"
libraryDependencies += "org.tpolecat"               %% "doobie-hikari" % "0.8.4"
libraryDependencies += "org.tpolecat"               %% "doobie-h2"     % "0.8.6"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
