package name.neuhalfen.todosimple.domain.model

import java.util.UUID

case class EventId(id: UUID) {
  if (null == id) throw new IllegalArgumentException("id must not be NULL")

  def this() = this(UUID.randomUUID())

  def this(idstr: String) = this(UUID.fromString(idstr))

  override def toString: String = s"${id.toString}"
}

object EventId {
  def generateId(): EventId = new EventId()

  def fromString(s: String): EventId = new EventId(s)
}

