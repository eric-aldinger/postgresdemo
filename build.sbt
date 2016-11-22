name := "postgresdemo"

version := "1.0"

scalaVersion := "2.11.8"

mainClass in Compile := Some("Application")

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.0.0",
  "com.zaxxer" % "HikariCP" % "2.4.1",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "org.postgresql" % "postgresql" % "9.4.1212",
  "org.scalatest" %% "scalatest" % "3.0.1"
)
