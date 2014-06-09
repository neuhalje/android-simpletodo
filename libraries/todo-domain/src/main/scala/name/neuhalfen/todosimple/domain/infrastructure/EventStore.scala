package name.neuhalfen.todosimple.domain.infrastructure

import name.neuhalfen.todosimple.domain.model.{AggregateRoot, UniqueId, TaskId, Event}
import java.io.IOException

trait EventStore[ENTITY <: AggregateRoot[ENTITY, Event[ENTITY]]] {
  @throws(classOf[IOException])
  def loadEvents(aggregateId: UniqueId[ENTITY]): Option[Seq[Event[ENTITY]]]

  @throws(classOf[IOException])
  def appendEvents(aggregateId: UniqueId[ENTITY], events: Seq[Event[ENTITY]]): Unit
}
