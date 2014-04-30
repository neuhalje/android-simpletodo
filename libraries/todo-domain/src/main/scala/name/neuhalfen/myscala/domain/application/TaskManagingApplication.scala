package name.neuhalfen.myscala.domain.application

import name.neuhalfen.myscala.domain.model.{Event, Command, Task}
import java.util.UUID
import javax.inject.Inject
import name.neuhalfen.myscala.domain.infrastructure.{TransactionRollbackException, Transaction, EventPublisher, EventStore}
import scala.collection.JavaConverters._
import scala.Some


class TaskManagingApplication @Inject()(eventStore: EventStore, eventPublishing: EventPublisher, tx: Transaction) {

  /*
   TODO: make execute command return the unpublished events, and kill publishEventsAfterCommit
   */
  def executeCommand(command: Command): Unit = {

    try {
      tx.beginTransaction()
      val aggregateId = command.aggregateRootId
      val events = eventStore.loadEvents(aggregateId)

      var task = events match {
        case Some(evts) => Task.loadFromHistory(evts)
        case None => Task.newInstance
      }

      task = task.handle(command)
      eventStore.appendEvents(task.id, task.uncommittedEVTs)
      eventPublishing.publishEventsInTransaction(task.uncommittedEVTs.asJava)
      tx.commit()
      publishEventsAfterCommitIgnoreExceptions(task.uncommittedEVTs.asJava)
    } catch {
      // FIXME: Make error reports better ...
      case e: TransactionRollbackException => throw new RuntimeException(e)
      case e: IllegalArgumentException => tx.rollback(); throw e
      case e: Any => tx.rollback(); throw new RuntimeException(e)
    }
    //task.markCommitted
  }


  def publishEventsAfterCommitIgnoreExceptions(events: java.util.List[Event]) {
    try {
      eventPublishing.publishEventsAfterCommit(events)
    } catch {
      case e: Exception => // Swallow the exception
    }
  }

  def loadTask(taskId: UUID): Option[Task] = {
    val events = eventStore.loadEvents(taskId)
    events match {
      case Some(e) => Some(Task.loadFromHistory(e))
      case None => None
    }

  }
}
