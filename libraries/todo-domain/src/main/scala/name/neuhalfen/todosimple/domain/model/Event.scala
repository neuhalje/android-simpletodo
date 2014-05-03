package name.neuhalfen.todosimple.domain.model

import java.util.UUID

sealed trait Event {
  val id: UUID
  val aggregateRootId: UUID
  val originalAggregateRootVersion: Int
  val newAggregateRootVersion: Int

  override def toString: String = s"${getClass.getSimpleName}: ${id.toString}. Aggregate: ${aggregateRootId.toString} v$originalAggregateRootVersion->v$newAggregateRootVersion"
}

case class TaskCreatedEvent(id: UUID, aggregateRootId: UUID, originalAggregateRootVersion: Int, newAggregateRootVersion: Int, description: String) extends Event {
  override def toString: String = super.toString() + s", description: $description"
}
case class TaskRenamedEvent(id: UUID, aggregateRootId: UUID, originalAggregateRootVersion: Int, newAggregateRootVersion: Int, newDescription: String) extends Event {
  override def toString: String = super.toString() + s", description: $newDescription"
}



