name := "server"

organization := "pl.klaudiabis"

version := "1.0"

scalaVersion := "2.11.7"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers += "krasserm at bintray" at "http://dl.bintray.com/krasserm/maven"

libraryDependencies ++= {
  val akkaV = "2.4.0"
  val akkaStreamV = "1.0"
  val scalaTestV = "2.2.5"
  Seq(
    "com.typesafe.akka" %% "akka-cluster" % akkaV,
    "com.typesafe.akka" %% "akka-cluster-tools" % akkaV,
    "com.typesafe.akka" %% "akka-cluster-sharding" % akkaV,
    "com.typesafe.akka" %% "akka-stream-experimental" % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-core-experimental" % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-experimental" % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-testkit-experimental" % akkaStreamV,
    "com.typesafe.akka" %% "akka-slf4j" % akkaV,
    "com.typesafe.akka" %% "akka-contrib" % akkaV,
    "com.typesafe.akka" %% "akka-persistence" % akkaV,
    "com.github.krasserm" %% "akka-persistence-cassandra" % "0.4",
    "org.iq80.leveldb" % "leveldb" % "0.7",
    "org.scalaz" %% "scalaz-core" % "7.1.1",
    "ch.qos.logback" % "logback-classic" % "1.1.3",
    "com.nimbusds" % "nimbus-jose-jwt" % "4.7",
    "joda-time" % "joda-time" % "2.9.1",
    "org.apache.commons" % "commons-email" % "1.4",
    "org.scalatest" %% "scalatest" % scalaTestV % "test"
  )
}

mainClass in assembly := Some("pl.klaudiabis.main.MonolithApp")