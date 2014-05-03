package name.neuhalfen.todosimple.domain.model

import _root_.name.neuhalfen.todosimple.test.UnitSpec
import _root_.name.neuhalfen.todosimple.test.UnitSpec._

class CreateTaskCommandTest extends UnitSpec with TaskTestTrait {

  "Creating a task " should " return only a TaskCreatedEvent with the correct description" in {
    val task = createUncommitedTaskViaCreateTaskCommand()
    assert(task.uncommittedEVTs.size == 1)

    for (evt <- task.uncommittedEVTs) evt match {
      case TaskCreatedEvent(eventId, aggregateRootId, oldAggregateVersion, newAggregateVersion, newDescription) => assert(newDescription == "fresh")
      case _ => fail("Unexpected event")
    }
  }

  "A create task command " should " base on an aggregate version of zero " in {
    val tc = new CreateTaskCommand(COMMAND_ID_ONE, TASK_ID_ONE, "description")
    assert(tc.aggregateRootVersion == 0)
  }

  "A created task " should " reject further create commands" in {
    val task = loadExistingTask()
    val tc = new CreateTaskCommand(COMMAND_ID_TWO, task.id, "description")

    an[IllegalArgumentException] should be thrownBy task.handle(tc)
  }

  it should " be in state CREATED" in {
    val task = createUncommitedTaskViaCreateTaskCommand()
    assert(task.state == TaskState.CREATED)
  }

}
