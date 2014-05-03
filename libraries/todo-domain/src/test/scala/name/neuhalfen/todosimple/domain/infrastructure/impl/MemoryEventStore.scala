package name.neuhalfen.todosimple.domain.infrastructure.impl

import java.util.UUID
import name.neuhalfen.todosimple.domain.model.Event
import name.neuhalfen.todosimple.domain.infrastructure.EventStore

class MemoryEventStore extends EventStore {
  val data = collection.mutable.Map[UUID, List[Event]]()

  override def appendEvents(aggregateId: UUID, events: Seq[Event]): Unit = {
    val stored = data getOrElse(aggregateId, List[Event]())
    data(aggregateId) = stored ++ events
  }

  override def loadEvents(aggregateId: UUID): Option[Seq[Event]] = {
    data get aggregateId
  }
}
