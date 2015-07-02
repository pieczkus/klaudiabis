package pl.klaudiabis.main

import akka.actor.{Props, ActorPath, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.io.IO
import akka.stream.{FlowMaterializer, ActorFlowMaterializer}
import akka.stream.scaladsl.Flow
import com.typesafe.config.{ConfigFactory, Config}
import pl.klaudiabis.product.ProductBoot

import scala.concurrent.ExecutionContextExecutor

trait Monolith {

  val actorSystem = "KlaudiaBis"

  /**
   * Implementations must return the entire ActorSystem configuraiton
   * @return the configuration
   */
  def config: Config

  /**
   * Implementations can perform any logic required to start or join a journal
   * @param system the ActorSystem that needs the journal starting or looking up
   * @param startStore ``true`` if this is the first time this function is being called
   * @param path the path for the actor that represents the journal
   */
  def journalStartUp(system: ActorSystem, startStore: Boolean, path: ActorPath): Unit

  /**
   * Starts up the Lift ActorSystem, binding Akka remoting to ``port`` and exposing all
   * rest services at ``0.0.0.0:restPort``.
   * @param port the Akka port
   * @param restPort the REST services port
   */
  final def actorSystemStartUp(port: Int, restPort: Int): Unit = {
    import scala.collection.JavaConverters._
    // Override the configuration of the port
    val firstSeedNodePort = (for {
      seedNode <- config.getStringList("akka.cluster.seed-nodes").asScala
      port <- ActorPath.fromString(seedNode).address.port
    } yield port).head

    // Create an Akka system
    implicit val system = ActorSystem(actorSystem, ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port").withFallback(config))
    implicit val executor = system.dispatcher
    implicit val materializer: FlowMaterializer = ActorFlowMaterializer()
    // Startup the journal - typically this is only used when running locally with a levelDB journal
    journalStartUp(system, port == firstSeedNodePort, ActorPath.fromString(s"akka.tcp://$actorSystem@127.0.0.1:$firstSeedNodePort/user/store"))

    // boot the microservices
    val product = ProductBoot.boot
    //    val trades = UserTradeBoot.boot
    //val exercise = ExerciseBoot.boot(notification.notification, profile.userProfile)

    Http().bindAndHandle(product.route(system.dispatcher), "localhost", restPort)
  }
}
