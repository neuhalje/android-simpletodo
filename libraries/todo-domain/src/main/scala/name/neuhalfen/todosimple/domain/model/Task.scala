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

import name.neuhalfen.todosimple.domain.model.EntityState.EntityState
import org.joda.time.DateTime


object Task extends AggregateFactory[Task, Event[Task]] {


  override def applyEvent = {
    case event: TaskCreatedEvent => Task(event.aggregateRootId, event.newAggregateRootVersion, event :: Nil, event.title, event.description, EntityState.CREATED)
    case event => unhandled(event)
  }

  def newTask(command: CreateTaskCommand): Task = {
    applyEvent(new TaskCreatedEvent(EventId.generateId(), command.aggregateRootId, 0, 1, DateTime.now(), command.title, command.description))
  }

  override def newInstance = new Task(null, 0, List[Event[Task]](), "", "", EntityState.NOT_CREATED)
}

case class Task(
                 _aggregateId: TaskId,
                 _version: Int,
                 _uncommittedEvents: List[Event[Task]],
                 _title: String,
                 _description: String,
                 state: EntityState
                 ) extends AggregateRoot[Task, Event[Task]] {

  def id = _aggregateId

  def version = _version

  def handle(command: Command[Task]): Task = {
    command match {
      case c: CreateTaskCommand => createTask(c)
      case c: RenameTaskCommand => renameTask(c)
      case c: DeleteTaskCommand => deleteTask(c)
    }
  }

  private def createTask(command: CreateTaskCommand): Task = {
    requireCorrectAggregateVersion(command.aggregateRootVersion)
    requireState(EntityState.NOT_CREATED)

    applyEvent(new TaskCreatedEvent(EventId.generateId(), command.aggregateRootId, 0, 1, DateTime.now(), command.title, command.description))
  }

  /**
   * Delete is idempotent, deleting deleted tasks does noting
   * @param command
   * @return
   */
  private def deleteTask(command: DeleteTaskCommand): Task = {
    requireCorrectAggregateId(command.aggregateRootId)
    requireCorrectAggregateVersion(command.aggregateRootVersion)

    if (EntityState.DELETED == state) {
      this
    } else {
      requireState(EntityState.CREATED)
      applyEvent(new TaskDeletedEvent(EventId.generateId(), id, version, version + 1, DateTime.now()))
    }
  }

  private def renameTask(command: RenameTaskCommand): Task = {
    requireCorrectAggregateId(command.aggregateRootId)
    requireCorrectAggregateVersion(command.aggregateRootVersion)
    requireState(EntityState.CREATED)

    if (_description.equals(command.newDescription) && _title.equals(command.newTitle)) {
      this
    } else {
      applyEvent(new TaskRenamedEvent(EventId.generateId(), id, version, version + 1, DateTime.now(), command.newTitle, command.newDescription))
    }
  }

  override def applyEvent = {
    case event: Event[Task] => {
      // The guard needs to let through "task created" events.
      val taskIsNew = (id == null)
      require(event.aggregateRootId == id || taskIsNew, s"wrong aggregate root '${event.aggregateRootId}', should be '$id'")
      require(event.originalAggregateRootVersion == version, s"wrong aggregate version ${event.originalAggregateRootVersion}, should be $version")

      event match {
        case TaskCreatedEvent(_, aggregateRootId, _, newAggregateVersion, _, newTitle, newDescription) =>
          copy(aggregateRootId, newAggregateVersion, _uncommittedEvents :+ event, newTitle, newDescription, EntityState.CREATED)

        case TaskRenamedEvent(_, _, _, newAggregateVersion, _, newTitle, newDescription) =>
          copy(id, newAggregateVersion, _uncommittedEvents :+ event, newTitle, newDescription, state)

        case TaskDeletedEvent(_, _, _, newAggregateVersion, _) =>
          copy(id, newAggregateVersion, _uncommittedEvents :+ event, _title, _description, EntityState.DELETED)
      }
    }
  }

  override def toString: String = s"$id@v${_version}: '${_description}'"

  override def markCommitted: Task = copy(_uncommittedEvents = Nil)

  override def uncommittedEVTs: Seq[Event[Task]] = _uncommittedEvents

  protected def requireState(requiredState: EntityState) {
    require(state == requiredState, s"State of task ${_aggregateId} v$version must be $requiredState but is $state")
  }

  protected def requireCorrectAggregateId(aggregateRootIdFromExternal: TaskId) {
    require(id == aggregateRootIdFromExternal, s"wrong aggregate root '$aggregateRootIdFromExternal', should be '$id'")
  }

  protected def requireCorrectAggregateVersion(versionFromExternal: Int) {
    require(versionFromExternal == version, s"wrong aggregate version $versionFromExternal, should be $version")
  }
}
