package name.neuhalfen.myscala.domain.infrastructure

import name.neuhalfen.myscala.domain.application.EventStore
import java.util.UUID
import name.neuhalfen.myscala.domain.model.Event

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
