ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.8"

val zioVersion = "2.0.0-RC5"
val zHttpVersion = "2.0.0-RC7"
val tapirVersion = "1.0.0-M9"
val circeVersion = "0.14.1"

ThisBuild / libraryDependencies ++= Seq(
  //core
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-test" % zioVersion % Test,
  "dev.zio" %% "zio-test-sbt" % zioVersion % Test,
  //config
  "com.github.pureconfig" %% "pureconfig" % "0.17.1",
  //logger
  "org.slf4j" % "slf4j-api" % "1.7.36",
  "ch.qos.logback" % "logback-classic" % "1.2.11",
  "io.logz.logback" % "logzio-logback-appender" % "1.0.24",
  "dev.zio" %% "zio-logging-slf4j" % "2.0.0-RC8",
  //sql
  "io.getquill" %% "quill-jdbc-zio" % "3.17.0-RC3",
  "org.postgresql" % "postgresql" % "42.3.4",
  //migrations
  "org.flywaydb" % "flyway-core" % "8.5.10",
  //http
  "io.d11" %% "zhttp" % zHttpVersion,
  "io.d11" %% "zhttp-test" % zHttpVersion % Test,
  //tapir
  "com.softwaremill.sttp.tapir" %% "tapir-zio" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion,
  //json
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-generic-extras" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  //util
  "io.scalaland" %% "chimney" % "0.6.1",
  "io.estatico" %% "newtype" % "0.4.4",
  "dev.zio" %% "zio-prelude" % "1.0.0-RC13",
  "com.github.daddykotex" %% "courier" % "3.1.0"
)

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

lazy val root = (project in file("."))
  .settings(
    name := "coursework"
  )

scalacOptions ++= Seq(
  "-Ymacro-annotations",
  "-language:implicitConversions",
  "-Xfatal-warnings",
  "-deprecation",
  "-feature"
)

// deployment
enablePlugins(JavaAppPackaging)
//enablePlugins(DockerPlugin)
//enablePlugins(AshScriptPlugin)
//dockerBaseImage := "openjdk:jre-alpine"
Compile / herokuAppName := "tfs-eventus"
Compile / herokuJdkVersion := "11"
