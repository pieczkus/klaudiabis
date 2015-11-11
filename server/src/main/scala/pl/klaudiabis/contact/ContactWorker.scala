package pl.klaudiabis.contact

import java.util.UUID

import akka.actor.SupervisorStrategy.{Restart, Stop}
import akka.actor._
import akka.cluster.client.ClusterClient.SendToAll
import pl.klaudiabis.contact.ContactMaster.{Ack, WorkIsReady}
import pl.klaudiabis.contact.ContactWorker._

import scala.concurrent.duration.Duration

object ContactWorker {

  def props(clusterClient: ActorRef, workExecutorProps: Props): Props = Props(classOf[ContactWorker], clusterClient, workExecutorProps)

  case class RegisterWorker(workerId: String)

  case class WorkerRequestsWork(workerId: String)

  case class WorkIsDone(workerId: String, workId: String, result: Any)

  case class WorkFailed(workerId: String, workId: String)

  case class WorkComplete(result: Any)

}

class ContactWorker(clusterClient: ActorRef, workExecutorProps: Props) extends Actor with ActorLogging {

  import pl.klaudiabis.common.Timeouts.defaults._

  val workerId = UUID.randomUUID().toString
  val masterPath = "/user/master/singleton"

  var currentWorkId: Option[String] = None

  def workId: String = currentWorkId match {
    case Some(workId) => workId
    case None => throw new IllegalStateException("Not working")
  }

  @scala.throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    sendToMaster(RegisterWorker(workerId))
  }

  val workExecutor = context.watch(context.actorOf(workExecutorProps, "exec"))

  override def supervisorStrategy = OneForOneStrategy() {
    case _: ActorInitializationException => Stop
    case _: DeathPactException => Stop
    case _: Exception =>
      currentWorkId foreach { workId => sendToMaster(WorkFailed(workerId, workId))}
      context.become(idle)
      Restart
  }


  override def receive = idle

  def idle: Receive = {
    case WorkIsReady =>
      sendToMaster(WorkerRequestsWork(workerId))

    case Work(workId, job) =>
      log.info("Got work: {}", job)
      currentWorkId = Some(workId)
      workExecutor ! job
      context.become(working)
  }

  def working: Receive = {
    case WorkComplete(result) =>
      log.info("Work is complete. Result {}.", result)
      sendToMaster(WorkIsDone(workerId, workId, result))
      context.setReceiveTimeout(contactWorkTimeout)
      context.become(waitForWorkIsDoneAck(result))

    case _: Work =>
      log.info("Yikes. Master told me to do work, while I'm working.")
  }

  def waitForWorkIsDoneAck(result: Any): Receive = {
    case Ack(id) if id == workId =>
      sendToMaster(WorkerRequestsWork(workerId))
      context.setReceiveTimeout(Duration.Undefined)
      context.become(idle)
    case ReceiveTimeout =>
      log.info("No ack from master, retrying")
      sendToMaster(WorkIsDone(workerId, workId, result))
  }

  override def unhandled(message: Any): Unit = message match {
    case Terminated(`workExecutor`) => context.stop(self)
    case WorkIsReady =>
    case _ => super.unhandled(message)
  }

  def sendToMaster(msg: Any): Unit = {
    clusterClient ! SendToAll(masterPath, msg)
  }

}
