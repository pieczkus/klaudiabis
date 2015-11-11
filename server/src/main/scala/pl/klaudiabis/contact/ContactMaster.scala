package pl.klaudiabis.contact

import akka.actor.{Terminated, ActorRef, ActorLogging}
import akka.cluster.client.ClusterClientReceptionist
import akka.persistence.{SnapshotOffer, PersistentActor}
import pl.klaudiabis.contact.ContactMaster._
import pl.klaudiabis.contact.WorkState._

import scala.concurrent.duration.Deadline

object ContactMaster {

  case object WorkIsReady

  case class Ack(id: String)

  private sealed trait WorkerStatus

  private case object Idle extends WorkerStatus

  private case class Busy(workId: String, deadline: Deadline) extends WorkerStatus

  private case class WorkerState(ref: ActorRef, status: WorkerStatus)

}

class ContactMaster extends PersistentActor with ActorLogging {

  import pl.klaudiabis.common.Timeouts.defaults._

  override def persistenceId: String = "contact-master"

  ClusterClientReceptionist(context.system).registerService(self)

  // workers state is not event sourced
  private var workers = Map[String, WorkerState]()

  // workState is event sourced
  private var workState = WorkState.empty

  override def receiveCommand: Receive = {
    case ContactWorker.RegisterWorker(workerId) =>
      val worker = sender()
      context.watch(worker)
      if (workers.contains(workerId)) {
        workers += (workerId -> workers(workerId).copy(ref = worker))
      } else {
        log.info("Worker registered: {}", workerId)
        workers += (workerId -> WorkerState(worker, status = Idle))
        if (workState.hasWork)
          worker ! WorkIsReady
      }

    case ContactWorker.WorkerRequestsWork(workerId) =>
      if (workState.hasWork) {
        workers.get(workerId) match {
          case Some(s@WorkerState(_, Idle)) =>
            val work = workState.nextWork
            persist(WorkStarted(work.workId)) { event =>
              workState = workState.updated(event)
              log.info("Giving worker {} some work {}", workerId, work.workId)
              workers += (workerId -> s.copy(status = Busy(work.workId, Deadline.now + defaultContactTimeout)))
              sender() ! work
            }
          case _ =>
        }
      }

    case ContactWorker.WorkIsDone(workerId, workId, result) =>
      // idempotent
      if (workState.isDone(workId)) {
        // previous Ack was lost, confirm again that this is done
        sender() ! Ack(workId)
      } else if (!workState.isInProgress(workId)) {
        log.info("Work {} not in progress, reported as done by worker {}", workId, workerId)
      } else {
        log.info("Work {} is done by worker {}", workId, workerId)
        changeWorkerToIdle(workerId, workId)
        persist(WorkCompleted(workId, result)) { event ⇒
          workState = workState.updated(event)
          // Ack back to original sender
          sender ! Ack(workId)
        }
      }

    case ContactWorker.WorkFailed(workerId, workId) =>
      if (workState.isInProgress(workId)) {
        log.info("Work {} failed by worker {}", workId, workerId)
        changeWorkerToIdle(workerId, workId)
        persist(WorkerFailed(workId)) { event ⇒
          workState = workState.updated(event)
          notifyWorkers()
        }
      }

    case work: Work =>
      // idempotent
      if (workState.isAccepted(work.workId)) {
        sender() ! Ack(work.workId)
      } else {
        log.info("Accepted work: {}", work.workId)
        persist(WorkAccepted(work)) { event ⇒
          // Ack back to original sender
          sender() ! Ack(work.workId)
          workState = workState.updated(event)
          saveSnapshot(workState)
          notifyWorkers()
        }
      }

    case Terminated(worker) =>
      workers = workers.filter(_._2.ref != worker)
  }

  def notifyWorkers(): Unit =
    if (workState.hasWork) {
      // could pick a few random instead of all
      workers.foreach {
        case (_, WorkerState(ref, Idle)) => ref ! WorkIsReady
        case _ => // busy
      }
    }

  def changeWorkerToIdle(workerId: String, workId: String): Unit =
    workers.get(workerId) match {
      case Some(s@WorkerState(_, Busy(`workId`, _))) ⇒
        workers += (workerId -> s.copy(status = Idle))
      case _ ⇒
      // ok, might happen after standby recovery, worker state is not persisted
    }

  override def receiveRecover: Receive = {
    case event: WorkDomainEvent =>
      // only update current state by applying the event, no side effects
      workState = workState.updated(event)
      log.info("Replayed {}", event.getClass.getSimpleName)
    case SnapshotOffer(_, snapshot: WorkState) =>
      workState = snapshot
  }


}
