package name.neuhalfen.todosimple.domain.model


sealed trait Event {
  val id: EventId
  val aggregateRootId: TaskId
  val originalAggregateRootVersion: Int
  val newAggregateRootVersion: Int

  override def toString: String = s"${getClass.getSimpleName}: ${id.toString}. Aggregate: ${aggregateRootId.toString} v$originalAggregateRootVersion->v$newAggregateRootVersion"
}

case class TaskCreatedEvent(id: EventId, aggregateRootId: TaskId, originalAggregateRootVersion: Int, newAggregateRootVersion: Int, description: String) extends Event {
  override def toString: String = super.toString() + s", description: $description"
}

case class TaskRenamedEvent(id: EventId, aggregateRootId: TaskId, originalAggregateRootVersion: Int, newAggregateRootVersion: Int, newDescription: String) extends Event {
  override def toString: String = super.toString() + s", description: $newDescription"
}

case class TaskDeletedEvent(id: EventId, aggregateRootId: TaskId, originalAggregateRootVersion: Int, newAggregateRootVersion: Int) extends Event {
  override def toString: String = super.toString()
}



