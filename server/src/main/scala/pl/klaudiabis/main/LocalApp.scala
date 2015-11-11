package pl.klaudiabis.main

import akka.actor._
import akka.http.scaladsl.Http
import akka.persistence.journal.leveldb.{SharedLeveldbJournal, SharedLeveldbStore}
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}

object LocalApp extends App with Monolith {

  val actorSystemName = "KlaudiaBis"
  val role = "allInOne"
  val port = 2551

  implicit val system = ActorSystem(actorSystemName, config)
  implicit val executor = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  def startup(ports: Seq[String]): Unit = {
    ports.foreach {
      p =>
        if (p.equals("2551")) {
          journalStartUp(ActorPath.fromString(s"akka.tcp://$actorSystemName@127.0.0.1:$p/user/store"))
        }
        Http().bindAndHandle(route, "localhost", 10000 + p.toInt)
        Thread.sleep(5000)
    }
  }

  /**
   * Implementations must return the entire ActorSystem configuraiton
   * @return the configuration
   */
  def config: Config = {
    val clusterShardingConfig = ConfigFactory.parseString(s"akka.contrib.cluster.sharding.role=$role")
    val clusterRoleConfig = ConfigFactory.parseString(s"akka.cluster.roles=[$role]")
    val clusterPort = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")

    clusterShardingConfig
      .withFallback(clusterRoleConfig)
      .withFallback(clusterPort)
      .withFallback(ConfigFactory.load("main.conf"))
  }

  /**
   * Implementations can perform any logic required to start or join a journal
   * @param path the path for the actor that represents the journal
   */
  def journalStartUp(path: ActorPath): Unit = {
    import akka.pattern.ask

    import scala.concurrent.duration._
    // Start the shared journal one one node (don't crash this SPOF)
    // This will not be needed with a distributed journal
    system.actorOf(Props[SharedLeveldbStore], "store")

    // register the shared journal
    implicit val timeout = Timeout(15.seconds)
    val f = system.actorSelection(path) ? Identify(None)

    f.onSuccess {
      case ActorIdentity(_, Some(ref)) => SharedLeveldbJournal.setStore(ref, system)
      case _ =>
        system.log.error("Shared journal not started at {}", path)
        system.terminate()
    }
    f.onFailure {
      case _ =>
        system.log.error("Lookup of shared journal at {} timed out", path)
        system.terminate()
    }
  }

  if (args.isEmpty) {
    startup(Seq("2551", "2552"))
  }
  else {
    startup(args)
  }

}
