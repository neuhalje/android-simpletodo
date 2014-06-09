package name.neuhalfen.todosimple.domain.application

import javax.inject.Inject
import name.neuhalfen.todosimple.domain.infrastructure.{TransactionRollbackException, Transaction, EventPublisher, EventStore}
import name.neuhalfen.todosimple.domain.model._
import scala.Some

trait Cache[T <: AggregateRoot[T, Event[T]]] {

  def put(aggregate: T): Unit

  def get(aggregateId: UniqueId[T]): Option[T]
}

class TaskManagingApplication @Inject()(eventStore: EventStore[Task], eventPublishing: EventPublisher[Task], tx: Transaction, cache: Cache[Task])  extends CommandExecutionApplication[Task](eventStore, eventPublishing, tx, cache)  {
  def newEntityInstance: Task = Task.newInstance
  def loadEntityFromHistory(events: Seq[Event[Task]]): Task = Task.loadFromHistory(events)

}

abstract class CommandExecutionApplication[ENTITY <: AggregateRoot[ENTITY, Event[ENTITY]]] (eventStore: EventStore[ENTITY], eventPublishing: EventPublisher[ENTITY], tx: Transaction, cache: Cache[ENTITY]) {

  def newEntityInstance: ENTITY
  def loadEntityFromHistory(events: Seq[Event[ENTITY]]): ENTITY

  def executeCommand(command: Command[ENTITY]): Unit = {

    try {
      tx.beginTransaction()
      val aggregateId = command.aggregateRootId

      val loadedEntity: Option[ENTITY] = cache.get(aggregateId).orElse(loadFromEventStore(aggregateId))

      val entity = loadedEntity.getOrElse(newEntityInstance)
      val augementedEntity = entity.handle(command)

      val uncommittedEVTs: Seq[Event[ENTITY]] = augementedEntity.uncommittedEVTs
      eventStore.appendEvents(augementedEntity.id, uncommittedEVTs)
      eventPublishing.publishEventsInTransaction(uncommittedEVTs)
      tx.commit()
      cache.put(augementedEntity.markCommitted)
      publishEventsAfterCommitIgnoreExceptions(uncommittedEVTs)
    } catch {
      // FIXME: Make error reports better ...
      case e: TransactionRollbackException => throw new RuntimeException(e)
      case e: IllegalArgumentException => tx.rollback(); throw e
      case e: Any => tx.rollback(); throw new RuntimeException(e)
    }
  }


  protected def loadFromEventStore(aggregateId: UniqueId[ENTITY]): Option[ENTITY] = {
    val events = eventStore.loadEvents(aggregateId)

    events match {
      case Some(evts) => Some(loadEntityFromHistory(evts))
      case None => None
    }
  }

  protected def publishEventsAfterCommitIgnoreExceptions(events: Seq[Event[ENTITY]]) {
    try {
      eventPublishing.publishEventsAfterCommit(events)
    } catch {
      case e: Exception => // Swallow the exception
    }
  }

  def loadEntity(entityId: UniqueId[ENTITY]): Option[ENTITY] = {
    tx.beginTransaction()
    val loadedEntity: Option[ENTITY] = cache.get(entityId).orElse(loadFromEventStore(entityId))
    tx.commit()
    if (loadedEntity.nonEmpty) {
      cache.put(loadedEntity.get)
    }
    loadedEntity
  }
}
