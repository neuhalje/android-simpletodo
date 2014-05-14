package name.neuhalfen.todosimple.domain.infrastructure

import name.neuhalfen.todosimple.domain.model.{TaskId, Event}
import java.io.IOException

trait EventStore {
  @throws(classOf[IOException])
  def loadEvents(aggregateId: TaskId): Option[Seq[Event]]

  @throws(classOf[IOException])
  def appendEvents(aggregateId: TaskId, events: Seq[Event]): Unit
}
