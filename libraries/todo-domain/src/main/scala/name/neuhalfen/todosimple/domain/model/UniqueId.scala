package name.neuhalfen.todosimple.domain.model

import java.util.UUID

abstract class UniqueId[T](id:UUID) {
  if (null == id) throw new IllegalArgumentException("id must not be NULL")

  override def toString: String = s"${id.toString}"
}

