package name.neuhalfen.todosimple.domain.model

import java.util.UUID

case class TaskId(id: UUID) {
  if (null == id) throw new IllegalArgumentException("id must not be NULL")

  def this() = this(UUID.randomUUID())

  def this(idstr: String) = this(UUID.fromString(idstr))

  override def toString: String = s"${id.toString}"
}

object TaskId {
  def generateId(): TaskId = new TaskId()

  def fromString(s: String): TaskId = new TaskId(s)
}
