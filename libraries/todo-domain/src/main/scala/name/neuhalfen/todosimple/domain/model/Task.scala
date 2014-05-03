package name.neuhalfen.todosimple.domain.model

import java.util.UUID

object Task extends AggregateFactory[Task, Event] {


  override def applyEvent = {
    case event: TaskCreatedEvent => Task(event.aggregateRootId, event.newAggregateRootVersion, event :: Nil, event.description)
    case event => unhandled(event)
  }

  def newTask(command: CreateTaskCommand): Task = {
    applyEvent(new TaskCreatedEvent(UUID.randomUUID(), command.aggregateRootId, 0, 1, command.description))
  }

  override def newInstance = new Task(null, 0, List[Event](), "")
}

case class TaskSummary(aggregateId: UUID, version: Int, description: String)

case class Task(
                 _aggregateId: UUID,
                 _version: Int,
                 _uncommittedEvents: List[Event],
                 _description: String
                 ) extends AggregateRoot[Task, Event] {

  def id = _aggregateId

  def version = _version


  /**
   * @return immutable summary of the task (mainly used for testing)
   */
  def taskSummary: TaskSummary = TaskSummary(id, version, _description)


  def handle(command: Command): Task = {
    command match {
      case c: RenameTaskCommand => renameTask(c)
      case c: CreateTaskCommand => createTask(c)
    }
  }

  private def createTask(command: CreateTaskCommand): Task = {
    applyEvent(new TaskCreatedEvent(UUID.randomUUID(), command.aggregateRootId, 0, 1, command.description))
  }

  private def renameTask(command: RenameTaskCommand): Task = {
    require(command.aggregateRootId == id, s"wrong aggregate root '${command.aggregateRootId}', should be '$id'")
    require(command.aggregateRootVersion == version, s"wrong aggregate version ${command.aggregateRootVersion}, should be $version")

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
          copy(aggregateRootId, newAggregateVersion, _uncommittedEvents :+ event, newDescription)
        case TaskRenamedEvent(_, _, _, newAggregateVersion, newDescription) =>
          copy(id, newAggregateVersion, _uncommittedEvents :+ event, newDescription)
      }
    }
  }

  override def toString: String = s"$id@v${_version}: '${_description}'"

  override def markCommitted: Task = copy(_uncommittedEvents = Nil)

  override def uncommittedEVTs: Seq[Event] = _uncommittedEvents
}
