package name.neuhalfen.todosimple.domain.model

import java.util.UUID

sealed  trait Command {
  val id: UUID
  val aggregateRootId: UUID
  val aggregateRootVersion: Int

  override def toString: String = s"${getClass.getSimpleName}: ${id.toString}. Aggregate: ${aggregateRootId.toString} v$aggregateRootVersion"
}


object Commands {
  def createTask(description : String) = CreateTaskCommand(UUID.randomUUID(), UUID.randomUUID(), description,0)
  def renameTask(task : Task, newDescription : String) = RenameTaskCommand(UUID.randomUUID(),  task.id, task.version, newDescription)
}

case class CreateTaskCommand(id: UUID, aggregateRootId: UUID, description: String, aggregateRootVersion: Int = 0) extends Command {
  override def toString: String = super.toString() + s", taskDesc: $description"
}

case class RenameTaskCommand(id: UUID, aggregateRootId: UUID,  aggregateRootVersion: Int,newDescription: String) extends Command {
  override def toString: String = super.toString() + s", taskDesc: $newDescription"
}
