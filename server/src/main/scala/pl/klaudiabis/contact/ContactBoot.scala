package pl.klaudiabis.contact

import akka.actor._
import akka.cluster.client.{ClusterClientSettings, ClusterClient}
import akka.cluster.singleton.{ClusterSingletonProxy, ClusterSingletonProxySettings, ClusterSingletonManagerSettings, ClusterSingletonManager}
import collection.JavaConversions._


case class ContactBoot(contactMaster: ActorRef, contactWorker: ActorRef, masterProxy: ActorRef) {

}

object ContactBoot {

  def boot(implicit system: ActorSystem): ContactBoot = {

    val contactMaster = system.actorOf(
      ClusterSingletonManager.props(
        ContactMaster.props,
        PoisonPill,
        ClusterSingletonManagerSettings(system)
      ),
      "master")

    val initialContacts = system.settings.config.getStringList("akka.contact-points").toList map { cp: String => ActorPath.fromString(cp)}

    val clusterClient = system.actorOf(
      ClusterClient.props(
        ClusterClientSettings(system).withInitialContacts(initialContacts.to[Set])),
      "clusterClient")

    val config = system.settings.config
    val smtpConfig = SmtpConfig(user = config.getString("smtp.user"), password = config.getString("smtp.password"), host = config.getString("smtp.host"))
    val contactWorker = system.actorOf(ContactWorker.props(clusterClient, Props(classOf[ContactWorkExecutor], smtpConfig)), "worker")

    val masterProxy = system.actorOf(
      ClusterSingletonProxy.props(
        settings = ClusterSingletonProxySettings(system),
        singletonManagerPath = "/user/master"
      ),
      name = "masterProxy")

    ContactBoot(contactMaster, contactWorker, masterProxy)
  }
}
