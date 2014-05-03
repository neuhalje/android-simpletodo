package name.neuhalfen.todosimple.domain.model

import _root_.name.neuhalfen.todosimple.test.UnitSpec

class RenameTaskCommandTest extends UnitSpec with TaskTestTrait {

  "Renaming a loaded task " should " return (only) a TaskRenamedEvent with the correct description" in {
    val originalTask = loadExistingTask()

    val task = originalTask.handle(Commands.renameTask(originalTask, "new description"))
    assert(task.uncommittedEVTs.size == 1)

    for (evt <- task.uncommittedEVTs) evt match {
      case TaskRenamedEvent(eventId, aggregateRootId, oldAggregateVersion, newAggregateVersion, newDescription) => assert(newDescription == "new description")
      case _ => fail("Unexpected event")
    }
  }

  "Renaming a freshly created task " should " return a Created, and a Renamed event with the correct description" in {
    val originalTask = createUncommitedTaskViaCreateTaskCommand()

    // TODO: This does not test the order of events
    val task = originalTask.handle(Commands.renameTask(originalTask, "new description"))

    assert(task.uncommittedEVTs.size == 2)
    for (evt <- task.uncommittedEVTs) evt match {
      case TaskCreatedEvent(eventId, aggregateRootId, oldAggregateVersion, newAggregateVersion, newDescription) => assert(newDescription == "fresh")
      case TaskRenamedEvent(eventId, aggregateRootId, oldAggregateVersion, newAggregateVersion, newDescription) => assert(newDescription == "new description")
      case _ => fail("Unexpected event")
    }
  }

  "Renaming a task to the same description " should " not issue an event " in {
    val originalTask = loadExistingTask()

    // TODO: This does not test the order of events
    val task = originalTask.handle(Commands.renameTask(originalTask, originalTask._description))
    assert(task.uncommittedEVTs.size == 0)
  }

}
