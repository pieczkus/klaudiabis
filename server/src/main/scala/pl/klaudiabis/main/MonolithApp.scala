package pl.klaudiabis.main

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

object MonolithApp extends App with Monolith {

  val actorSystemName = "KlaudiaBis"
  val role = "allInOne"

  implicit val system = ActorSystem(actorSystemName, config)
  implicit val executor = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  lazy val config = {
    val rawConfig = ConfigFactory.load("production.conf")

    val clusterShardingConfig = ConfigFactory.parseString(s"akka.contrib.cluster.sharding.role=$role")
    val clusterRoleConfig = ConfigFactory.parseString(s"akka.cluster.roles=[$role]")
    // As the following configuration values are lists supplied by environment variables, we need to work a little harder here to ensure they are parsed correctly
    val seedingConfig = ConfigFactory.parseString(s"akka.cluster.seed-nodes=[${rawConfig.getString("akka.cluster.seed-nodes").split(",").map(_.trim).mkString("\"", "\",\"", "\"")}]")
    val journalConfig = ConfigFactory.parseString(s"cassandra-journal.contact-points=[${rawConfig.getString("cassandra-journal.contact-points").split(",").map(_.trim).mkString("\"", "\",\"", "\"")}]")
    val snapshotConfig = ConfigFactory.parseString(s"cassandra-snapshot-store.contact-points=[${rawConfig.getString("cassandra-snapshot-store.contact-points").split(",").map(_.trim).mkString("\"", "\",\"", "\"")}]")
    seedingConfig
      .withFallback(journalConfig)
      .withFallback(snapshotConfig)
      .withFallback(clusterShardingConfig)
      .withFallback(clusterRoleConfig)
      .withFallback(rawConfig)
  }
  val httpPort = config.getInt("akka.rest.port")
  Http().bindAndHandle(route, "0.0.0.0", httpPort)

}
