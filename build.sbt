name := """animalia"""

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= {
  val akkaVersion = "2.4.8"
  Seq(
    "org.scala-lang" % "scala-reflect" % "2.11.8",
    "org.scalatest"       %%  "scalatest" % "3.0.1" % "test",
    "com.typesafe.akka"   %%  "akka-actor" % akkaVersion,
    "com.typesafe.akka"   %%  "akka-http" % "10.0.3",
    "com.typesafe.akka"   %%  "akka-http-testkit" % akkaVersion,
    "com.typesafe.akka"   %%  "akka-http-spray-json-experimental" % akkaVersion,
    "org.scala-lang.modules" %% "scala-xml" % "1.0.5",
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaVersion
  )
}

fork in run := true