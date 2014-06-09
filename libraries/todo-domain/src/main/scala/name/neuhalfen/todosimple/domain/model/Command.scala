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


sealed trait Command[ENTITY] {
  val id: CommandId[ENTITY]
  val aggregateRootId: UniqueId[ENTITY]
  val aggregateRootVersion: Int

  override def toString: String = s"${getClass.getSimpleName}: ${id.toString}. Aggregate: ${aggregateRootId.toString} v$aggregateRootVersion"
}


object Commands {
  def createTask(title: String, description: String) = CreateTaskCommand(CommandId.generateId(), TaskId.generateId(), title, description, 0)

  def renameTask(task: Task, newTitle: String, newDescription: String) = RenameTaskCommand(CommandId.generateId(), task.id, task.version, newTitle, newDescription)

  def deleteTask(task: Task) = DeleteTaskCommand(CommandId.generateId(), task.id, task.version)
}

case class CreateTaskCommand(id: CommandId[Task], aggregateRootId: TaskId, title: String, description: String, aggregateRootVersion: Int = 0) extends Command[Task] {
  override def toString: String = super.toString() + s", title: '$title', taskDesc: '$description'."
}

case class RenameTaskCommand(id: CommandId[Task], aggregateRootId: TaskId, aggregateRootVersion: Int, newTitle: String, newDescription: String) extends Command[Task] {
  override def toString: String = super.toString() + s", newTitle: '$newTitle', newDesc: '$newDescription'"
}

case class DeleteTaskCommand(id: CommandId[Task], aggregateRootId: TaskId, aggregateRootVersion: Int) extends Command[Task] {
  override def toString: String = super.toString()
}



case class CreateLabelCommand(id: CommandId[Label], aggregateRootId: LabelId, title: String,  aggregateRootVersion: Int = 0) extends Command[Label] {
  override def toString: String = super.toString() + s", title: '$title'."
}

case class RenameLabelCommand(id: CommandId[Label], aggregateRootId: LabelId, aggregateRootVersion: Int, newTitle: String) extends Command[Label] {
  override def toString: String = super.toString() + s", newTitle: '$newTitle'"
}

case class DeleteLabelCommand(id: CommandId[Label], aggregateRootId: LabelId, aggregateRootVersion: Int) extends Command[Label] {
  override def toString: String = super.toString()
}

