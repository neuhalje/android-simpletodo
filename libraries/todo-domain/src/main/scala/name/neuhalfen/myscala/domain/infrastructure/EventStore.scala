package name.neuhalfen.myscala.domain.infrastructure

import java.util.UUID
import name.neuhalfen.myscala.domain.model.Event

trait EventStore {
  def loadEvents(aggregateId: UUID): Option[Seq[Event]]

  def appendEvents(aggregateId: UUID, events: Seq[Event]): Unit
}
