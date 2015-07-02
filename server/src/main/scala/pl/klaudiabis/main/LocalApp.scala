package pl.klaudiabis.main

import akka.actor._
import akka.persistence.journal.leveldb.{SharedLeveldbJournal, SharedLeveldbStore}
import akka.util.Timeout
import com.typesafe.config.{ConfigFactory, Config}

import collection.JavaConversions._

object LocalApp extends App with Monolith {

  val role = "allInOne"

  /**
   * Implementations must return the entire ActorSystem configuraiton
   * @return the configuration
   */
  override def config: Config = {
    val clusterShardingConfig = ConfigFactory.parseString(s"akka.contrib.cluster.sharding.role=$role")
    val clusterRoleConfig = ConfigFactory.parseString(s"akka.cluster.roles=[$role]")

    clusterShardingConfig
      .withFallback(clusterRoleConfig)
      .withFallback(ConfigFactory.load("main.conf"))
  }

  /**
   * Implementations can perform any logic required to start or join a journal
   * @param system the ActorSystem that needs the journal starting or looking up
   * @param startStore ``true`` if this is the first time this function is being called
   * @param path the path for the actor that represents the journal
   */
  override def journalStartUp(system: ActorSystem, startStore: Boolean, path: ActorPath): Unit = {
    import akka.pattern.ask
    import scala.concurrent.duration._
    import system.dispatcher
    // Start the shared journal one one node (don't crash this SPOF)
    // This will not be needed with a distributed journal
    if (startStore) {
      system.actorOf(Props[SharedLeveldbStore], "store")
    }

    // register the shared journal
    implicit val timeout = Timeout(15.seconds)
    val f = system.actorSelection(path) ? Identify(None)

    f.onSuccess {
      case ActorIdentity(_, Some(ref)) => SharedLeveldbJournal.setStore(ref, system)
      case _ =>
        system.log.error("Shared journal not started at {}", path)
        system.shutdown()
    }
    f.onFailure {
      case _ =>
        system.log.error("Lookup of shared journal at {} timed out", path)
        system.shutdown()
    }
  }

  val ports = config.getIntList("akka.cluster.jvm-ports").toList
  ports.foreach(port => actorSystemStartUp(port, 10000 + port))

}
