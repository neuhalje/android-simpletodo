package name.neuhalfen.todosimple.domain.application

import javax.inject.Inject
import name.neuhalfen.todosimple.domain.infrastructure.{TransactionRollbackException, Transaction, EventPublisher, EventStore}
import name.neuhalfen.todosimple.domain.model._
import scala.Some
import scala.collection.JavaConverters._
import scala.collection.parallel.mutable
import scala.collection.mutable.Map._

trait Cache {
  def isCached(aggregateId: TaskId): Boolean

  def put(aggregate: Task): Unit

  def get(aggregateId: TaskId): Option[Task]
}

/**
 * FIXME: This is just a temporary solution; needs to be injected.
 */
private object Cache extends Cache{
  private val cache : scala.collection.mutable.Map[TaskId, Task] = scala.collection.mutable.Map.empty

  @Override
  def isCached(aggregateId: TaskId) : Boolean = {
    cache.contains(aggregateId)
  }

  @Override
  def put(aggregate: Task): Unit = {
    cache.put(aggregate.id, aggregate)
  }

  @Override
  def get(aggregateId: TaskId): Option[Task] = {
    cache.get(aggregateId)
  }

}

class TaskManagingApplication @Inject()(eventStore: EventStore, eventPublishing: EventPublisher, tx: Transaction) {

  /*
   TODO: make execute command return the unpublished events, and kill publishEventsAfterCommit
   */
  def executeCommand(command: Command): Unit = {

    try {
      tx.beginTransaction()
      val aggregateId = command.aggregateRootId

      // FIXME: flatten might help to clean this up

      val loadedTask : Task =  Cache.get(aggregateId) match {
        case Some(t) => t
        case None =>loadFromEventStore(aggregateId)
      }

      val augementedTask = loadedTask.handle(command)

      eventStore.appendEvents(augementedTask.id, augementedTask.uncommittedEVTs)
      eventPublishing.publishEventsInTransaction(augementedTask.uncommittedEVTs.asJava)
      tx.commit()
      Cache.put(augementedTask)
      publishEventsAfterCommitIgnoreExceptions(augementedTask.uncommittedEVTs.asJava)
    } catch {
      // FIXME: Make error reports better ...
      case e: TransactionRollbackException => throw new RuntimeException(e)
      case e: IllegalArgumentException => tx.rollback(); throw e
      case e: Any => tx.rollback(); throw new RuntimeException(e)
    }
    //task.markCommitted
  }


  def loadFromEventStore(aggregateId: TaskId): Task = {
    val events = eventStore.loadEvents(aggregateId)

    val task = events match {
      case Some(evts) => Task.loadFromHistory(evts)
      case None => Task.newInstance
    }
    task
  }

  def publishEventsAfterCommitIgnoreExceptions(events: java.util.List[Event]) {
    try {
      eventPublishing.publishEventsAfterCommit(events)
    } catch {
      case e: Exception => // Swallow the exception
    }
  }

  def loadTask(taskId: TaskId): Option[Task] = {
    tx.beginTransaction()
    val events = eventStore.loadEvents(taskId)
    tx.commit()
    events match {
      case Some(e) => Some(Task.loadFromHistory(e))
      case None => None
    }

  }
}
