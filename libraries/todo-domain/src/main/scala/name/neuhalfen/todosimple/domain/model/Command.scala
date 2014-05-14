package name.neuhalfen.todosimple.domain.model


sealed trait Command {
  val id: CommandId
  val aggregateRootId: TaskId
  val aggregateRootVersion: Int

  override def toString: String = s"${getClass.getSimpleName}: ${id.toString}. Aggregate: ${aggregateRootId.toString} v$aggregateRootVersion"
}


object Commands {
  def createTask(description: String) = CreateTaskCommand(CommandId.generateId(), TaskId.generateId(), description, 0)

  def renameTask(task: Task, newDescription: String) = RenameTaskCommand(CommandId.generateId(), task.id, task.version, newDescription)

  def deleteTask(task: Task) = DeleteTaskCommand(CommandId.generateId(), task.id, task.version)
}

case class CreateTaskCommand(id: CommandId, aggregateRootId: TaskId, description: String, aggregateRootVersion: Int = 0) extends Command {
  override def toString: String = super.toString() + s", taskDesc: $description"
}

case class RenameTaskCommand(id: CommandId, aggregateRootId: TaskId, aggregateRootVersion: Int, newDescription: String) extends Command {
  override def toString: String = super.toString() + s", taskDesc: $newDescription"
}

case class DeleteTaskCommand(id: CommandId, aggregateRootId: TaskId, aggregateRootVersion: Int) extends Command {
  override def toString: String = super.toString()
}
