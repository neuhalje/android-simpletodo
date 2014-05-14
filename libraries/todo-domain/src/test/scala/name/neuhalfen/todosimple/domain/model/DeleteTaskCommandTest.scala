package name.neuhalfen.todosimple.domain.model

import _root_.name.neuhalfen.todosimple.test.UnitSpec

class DeleteTaskCommandTest extends UnitSpec with TaskTestTrait {

  "deleting a loaded task " should " return (only) a TaskDeletedEvent" in {
    val originalTask = loadExistingTask()

    val task = originalTask.handle(Commands.deleteTask(originalTask))
    assert(task.uncommittedEVTs.size == 1)

    var isFound: Boolean = false

    for (evt <- task.uncommittedEVTs) evt match {
      case TaskDeletedEvent(_, _, _, _) => isFound = true
      case _ => fail("Unexpected event")
    }

    assert(isFound)
  }

  "deleting a freshly created task " should " returns (only) a created, and a deleted event" in {
    val originalTask = createUncommitedTaskViaCreateTaskCommand()

    val task = originalTask.handle(Commands.deleteTask(originalTask))
    assert(task.uncommittedEVTs.size == 2)

    var isFoundCreated: Boolean = false
    var isFoundDeleted: Boolean = false

    for (evt <- task.uncommittedEVTs) evt match {
      case TaskCreatedEvent(_, _, _, _, _) => isFoundCreated = true
      case TaskDeletedEvent(_, _, _, _) => isFoundDeleted = true
      case _ => fail("Unexpected event")
    }
    assert(isFoundCreated, "Task created event should be found")
    assert(isFoundDeleted, "Task deletd event should be found")
  }

  "Deleting a deleted task " should " be idempotent " in {
    val originalTask = loadExistingTask()

    val deletedTask = originalTask.handle(Commands.deleteTask(originalTask))
    val deletedTwiceTask = deletedTask.handle(Commands.deleteTask(deletedTask))


    assert(deletedTask.uncommittedEVTs.size == deletedTwiceTask.uncommittedEVTs.size)
    assert(deletedTask == deletedTwiceTask)
  }

  "deleting a task" should " change the state to CREATED" in {
    val originalTask = createUncommitedTaskViaCreateTaskCommand()
    assume(originalTask.state == TaskState.CREATED)

    val deletedTask = originalTask.handle(Commands.deleteTask(originalTask))
    assert(deletedTask.state == TaskState.DELETED)
  }

  "Renaming a deleted task " should " fail " in {
    val originalTask = loadExistingTask()
    val deletedTask = originalTask.handle(Commands.deleteTask(originalTask))

    an[IllegalArgumentException] should be thrownBy deletedTask.handle(Commands.renameTask(deletedTask, "die!"))
  }

}