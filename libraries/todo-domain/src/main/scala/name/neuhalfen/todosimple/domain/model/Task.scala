package name.neuhalfen.todosimple.domain.model

import java.util.UUID
import name.neuhalfen.todosimple.domain.model.TaskState.TaskState


object Task extends AggregateFactory[Task, Event] {


  override def applyEvent = {
    case event: TaskCreatedEvent => Task(event.aggregateRootId, event.newAggregateRootVersion, event :: Nil, event.description, TaskState.CREATED)
    case event => unhandled(event)
  }

  def newTask(command: CreateTaskCommand): Task = {
    applyEvent(new TaskCreatedEvent(UUID.randomUUID(), command.aggregateRootId, 0, 1, command.description))
  }

  override def newInstance = new Task(null, 0, List[Event](), "", TaskState.NOT_CREATED)
}

case class TaskSummary(aggregateId: UUID, version: Int, description: String)


case class Task(
                 _aggregateId: UUID,
                 _version: Int,
                 _uncommittedEvents: List[Event],
                 _description: String,
                 state: TaskState
                 ) extends AggregateRoot[Task, Event] {

  def id = _aggregateId

  def version = _version


  /**
   * @return immutable summary of the task (mainly used for testing)
   */
  def taskSummary: TaskSummary = TaskSummary(id, version, _description)


  def handle(command: Command): Task = {
    command match {
      case c: CreateTaskCommand => createTask(c)
      case c: RenameTaskCommand => renameTask(c)
      case c: DeleteTaskCommand => deleteTask(c)
    }
  }

  private def createTask(command: CreateTaskCommand): Task = {
    requireCorrectAggregateVersion(command.aggregateRootVersion)
    requireState(TaskState.NOT_CREATED)

    applyEvent(new TaskCreatedEvent(UUID.randomUUID(), command.aggregateRootId, 0, 1, command.description))
  }

  /**
   * Delete is idempotent, deleting deleted tasks does noting
   * @param command
   * @return
   */
  private def deleteTask(command: DeleteTaskCommand): Task = {
    requireCorrectAggregateId(command.aggregateRootId)
    requireCorrectAggregateVersion(command.aggregateRootVersion)

    if (TaskState.DELETED == state) {
      this
    } else {
      requireState(TaskState.CREATED)
      applyEvent(new TaskDeletedEvent(UUID.randomUUID(), id, version, version + 1))
    }
  }

  private def renameTask(command: RenameTaskCommand): Task = {
    requireCorrectAggregateId(command.aggregateRootId)
    requireCorrectAggregateVersion(command.aggregateRootVersion)
    requireState(TaskState.CREATED)

    if (_description.equals(command.newDescription)) {
      this
    } else {
      applyEvent(new TaskRenamedEvent(UUID.randomUUID(), id, version, version + 1, command.newDescription))
    }
  }

  override def applyEvent = {
    case event: Event => {
      // The guard needs to let through "task created" events.
      val taskIsNew = (id == null)
      require(event.aggregateRootId == id || taskIsNew, s"wrong aggregate root '${event.aggregateRootId}', should be '$id'")
      require(event.originalAggregateRootVersion == version, s"wrong aggregate version ${event.originalAggregateRootVersion}, should be $version")

      event match {
        case TaskCreatedEvent(_, aggregateRootId, _, newAggregateVersion, newDescription) =>
          copy(aggregateRootId, newAggregateVersion, _uncommittedEvents :+ event, newDescription, TaskState.CREATED)

        case TaskRenamedEvent(_, _, _, newAggregateVersion, newDescription) =>
          copy(id, newAggregateVersion, _uncommittedEvents :+ event, newDescription, state)

        case TaskDeletedEvent(_, _, _, newAggregateVersion) =>
          copy(id, newAggregateVersion, _uncommittedEvents :+ event, _description, TaskState.DELETED)
      }
    }
  }

  override def toString: String = s"$id@v${_version}: '${_description}'"

  override def markCommitted: Task = copy(_uncommittedEvents = Nil)

  override def uncommittedEVTs: Seq[Event] = _uncommittedEvents

  protected def requireState(requiredState: TaskState) {
    require(state == requiredState, s"State of task ${_aggregateId} v$version must be $requiredState but is $state")
  }

  protected def requireCorrectAggregateId(aggregateRootIdFromExternal: UUID) {
    require(id == aggregateRootIdFromExternal, s"wrong aggregate root '$aggregateRootIdFromExternal', should be '$id'")
  }

  protected def requireCorrectAggregateVersion(versionFromExternal: Int) {
    require(versionFromExternal == version, s"wrong aggregate version $versionFromExternal, should be $version")
  }
}
