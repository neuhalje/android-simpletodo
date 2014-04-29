package name.neuhalfen.myscala.domain.application

import name.neuhalfen.myscala.domain.model.Command
import name.neuhalfen.myscala.domain.model.Task
import java.util.UUID
import javax.inject.Inject
import name.neuhalfen.myscala.domain.infrastructure.{TransactionRollbackException, Transaction, EventPublisher, EventStore}
import scala.collection.JavaConverters._
import scala.Some


class TaskManagingApplication @Inject()(eventStore: EventStore, eventPublishing: EventPublisher, tx: Transaction) {

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
      eventPublishing.publishEventsAfterCommit(task.uncommittedEVTs.asJava)
    } catch {
      // FIXME: Make error reports better ...
      case e: TransactionRollbackException => throw new RuntimeException(e)
      case e: IllegalArgumentException => throw e
      case e: Any  => tx.rollback(); throw new RuntimeException(e)
    }
    //task.markCommitted
  }


  def loadTask(taskId: UUID): Option[Task] = {
    val events = eventStore.loadEvents(taskId)
    events match {
      case Some(e) => Some(Task.loadFromHistory(e))
      case None => None
    }

  }
}
