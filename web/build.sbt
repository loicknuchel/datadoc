name := "datadoc"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies ++= List(
  "org.http4s" %% "http4s-blaze-server" % "0.18.0-M8",
  "org.http4s" %% "http4s-circe" % "0.18.0-M8",
  "org.http4s" %% "http4s-dsl" % "0.18.0-M8",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test",
  "org.scalacheck" %% "scalacheck" % "1.13.4" % "test",
  "com.github.alexarchambault" %% "scalacheck-shapeless_1.13" % "1.1.6" % "test"
)

scalacOptions += "-Ypartial-unification"
