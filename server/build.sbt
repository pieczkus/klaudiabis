name := "server"

organization := "pl.klaudiabis"

version := "1.0"

scalaVersion := "2.11.7"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers += "krasserm at bintray" at "http://dl.bintray.com/krasserm/maven"

libraryDependencies ++= {
  val akkaV = "2.3.11"
  val akkaStreamV = "1.0"
  val scalaTestV = "2.2.4"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-stream-experimental" % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-core-experimental" % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-experimental" % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-testkit-experimental" % akkaStreamV,
    "com.typesafe.akka" %% "akka-cluster" % akkaV,
    "com.typesafe.akka" %% "akka-slf4j" % akkaV,
    "com.typesafe.akka" %% "akka-contrib" % akkaV intransitive(),
    "com.typesafe.akka" %% "akka-persistence-experimental" % akkaV intransitive(),
    "com.github.krasserm" %% "akka-persistence-cassandra" % "0.3.8" intransitive(),
    "org.iq80.leveldb" % "leveldb" % "0.7",
    "org.scalaz" %% "scalaz-core" % "7.1.1",
    "ch.qos.logback" % "logback-classic" % "1.1.3",
    "org.scalatest" %% "scalatest" % scalaTestV % "test"
  )
}

Revolver.settings
