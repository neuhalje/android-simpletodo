package name.neuhalfen.todosimple.domain.application

import javax.inject.Inject
import name.neuhalfen.todosimple.domain.infrastructure.{TransactionRollbackException, Transaction, EventPublisher, EventStore}
import name.neuhalfen.todosimple.domain.model._
import scala.Some

trait Cache[T <: AggregateRoot[T, Event[T]]] {

  def put(aggregate: T): Unit

  def get(aggregateId: UniqueId[T]): Option[T]
}


class TaskManagingApplication @Inject()(eventStore: EventStore[Task], eventPublishing: EventPublisher[Task], tx: Transaction, cache: Cache[Task]) {

  def executeCommand(command: Command[Task]): Unit = {

    try {
      tx.beginTransaction()
      val aggregateId = command.aggregateRootId

      val loadedTask: Option[Task] = cache.get(aggregateId).orElse(loadFromEventStore(aggregateId))

      val task = loadedTask.getOrElse(Task.newInstance)
      val augementedTask = task.handle(command)

      val uncommittedEVTs: Seq[Event[Task]] = augementedTask.uncommittedEVTs
      eventStore.appendEvents(augementedTask.id, uncommittedEVTs)
      eventPublishing.publishEventsInTransaction(uncommittedEVTs)
      tx.commit()
      cache.put(augementedTask.markCommitted)
      publishEventsAfterCommitIgnoreExceptions(uncommittedEVTs)
    } catch {
      // FIXME: Make error reports better ...
      case e: TransactionRollbackException => throw new RuntimeException(e)
      case e: IllegalArgumentException => tx.rollback(); throw e
      case e: Any => tx.rollback(); throw new RuntimeException(e)
    }
  }


  protected def loadFromEventStore(aggregateId: UniqueId[Task]): Option[Task] = {
    val events = eventStore.loadEvents(aggregateId)

    events match {
      case Some(evts) => Some(Task.loadFromHistory(evts))
      case None => None
    }
  }

  protected def publishEventsAfterCommitIgnoreExceptions(events: Seq[Event[Task]]) {
    try {
      eventPublishing.publishEventsAfterCommit(events)
    } catch {
      case e: Exception => // Swallow the exception
    }
  }

  def loadTask(taskId: TaskId): Option[Task] = {
    tx.beginTransaction()
    val loadedTask: Option[Task] = cache.get(taskId).orElse(loadFromEventStore(taskId))
    tx.commit()
    if (loadedTask.nonEmpty) {
      cache.put(loadedTask.get)
    }
    loadedTask
  }
}
