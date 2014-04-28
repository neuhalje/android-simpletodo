package name.neuhalfen.myscala.domain.model

import _root_.name.neuhalfen.myscala.test.UnitSpec
import _root_.name.neuhalfen.myscala.test.UnitSpec._

class CreateTaskCommandTest extends UnitSpec with TaskTestTrait {

  "Creating a task " should " return only a TaskCreatedEvent with the correct description" in {
    val task = createTaskViaCreateTaskCommand()

    for (evt <- task.uncommittedEVTs) evt match {
      case TaskCreatedEvent(eventId, aggregateRootId, oldAggregateVersion, newAggregateVersion, newDescription) => assert(newDescription == "fresh")
      case _ => fail("Unecpected event")
    }
  }

  "A create task command " should " base on an aggregate version of zero " in {
    val tc = new CreateTaskCommand(COMMAND_ID_ONE, TASK_ID_ONE,  "description")
    assert(tc.aggregateRootVersion == 0 )
  }

  "A created task " should " reject further create commands" in {
    val task = loadFreshTask()
    val tc = new CreateTaskCommand(COMMAND_ID_TWO, task.id,  "description")

    an[IllegalArgumentException] should be thrownBy task.handle(tc)
  }

}
