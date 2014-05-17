/*
Copyright 2014 Jens Neuhalfen

Licensed under the Apache License, Version 2.0 (the "License"); you may not use
this file except in compliance with the License. You may obtain a copy of the
License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed
under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
 */
package name.neuhalfen.todosimple.domain.model


sealed trait Command {
  val id: CommandId
  val aggregateRootId: TaskId
  val aggregateRootVersion: Int

  override def toString: String = s"${getClass.getSimpleName}: ${id.toString}. Aggregate: ${aggregateRootId.toString} v$aggregateRootVersion"
}


object Commands {
  def createTask(title: String, description: String) = CreateTaskCommand(CommandId.generateId(), TaskId.generateId(), title, description, 0)

  def renameTask(task: Task, newTitle: String, newDescription: String) = RenameTaskCommand(CommandId.generateId(), task.id, task.version, newTitle, newDescription)

  def deleteTask(task: Task) = DeleteTaskCommand(CommandId.generateId(), task.id, task.version)
}

case class CreateTaskCommand(id: CommandId, aggregateRootId: TaskId, title: String, description: String, aggregateRootVersion: Int = 0) extends Command {
  override def toString: String = super.toString() + s", title: '$title', taskDesc: '$description'."
}

case class RenameTaskCommand(id: CommandId, aggregateRootId: TaskId, aggregateRootVersion: Int, newTitle: String, newDescription: String) extends Command {
  override def toString: String = super.toString() + s", newTitle: '$newTitle', newDesc: '$newDescription'"
}

case class DeleteTaskCommand(id: CommandId, aggregateRootId: TaskId, aggregateRootVersion: Int) extends Command {
  override def toString: String = super.toString()
}
