package name.neuhalfen.todosimple.domain.infrastructure

import java.util.UUID
import name.neuhalfen.todosimple.domain.model.Event
import java.io.IOException

trait EventStore {
  @throws(classOf[IOException])
  def loadEvents(aggregateId: UUID): Option[Seq[Event]]

  @throws(classOf[IOException])
  def appendEvents(aggregateId: UUID, events: Seq[Event]): Unit
}
