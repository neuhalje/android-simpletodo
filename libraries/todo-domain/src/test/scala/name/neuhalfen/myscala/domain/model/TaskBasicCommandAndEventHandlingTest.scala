package name.neuhalfen.myscala.domain.model

import _root_.name.neuhalfen.myscala.test.UnitSpec
import _root_.name.neuhalfen.myscala.test.UnitSpec._

/**
 * Some generic tests to see, if event and command handling works properly
 */
class TaskBasicCommandAndEventHandlingTest extends UnitSpec with TaskTestTrait {


  "A task " should " report its id" in {
    val task = createTaskViaCreateTaskCommand()
    assert(task.id == TASK_ID_ONE)
  }

  it should "have a toString that includes the id" in {
    val task = createTaskViaCreateTaskCommand()
    task.toString() should include(task.id.toString())
  }

  "Running a command " should " return only a Events with the correct aggregate" in {
    val task = createTaskViaCreateTaskCommand()
    val tc = new RenameTaskCommand(COMMAND_ID_TWO, task.id, task.version, "change the description")
    task.handle(tc)
    for (evt <- task.uncommittedEVTs) {
      assert(evt.aggregateRootId == task.id)
    }
  }

  "Running a command " should " return events with ascending versions " in {
    var task = createTaskViaCreateTaskCommand()
    val tc = new RenameTaskCommand(COMMAND_ID_TWO, task.id, task.version, "change the description")

    var versionBeforeEvent = 0

    task = task.handle(tc)
    for (evt <- task.uncommittedEVTs) {
      assert(evt.aggregateRootId == task.id)
      assert(evt.originalAggregateRootVersion == versionBeforeEvent)
      versionBeforeEvent = evt.newAggregateRootVersion
    }
    assert(versionBeforeEvent == 2)
    assert(versionBeforeEvent == task.version)
  }

  it should "fail, if the version of the aggregate does not match the version in the command" in {
    val task = createTaskViaCreateTaskCommand()
    val tc = new RenameTaskCommand(COMMAND_ID_TWO, task.id, task.version + 1, "change the description")
    an[IllegalArgumentException] should be thrownBy task.handle(tc)
  }



  it should "fail, if the the aggregate does not match the command" in {
    val task = createTaskViaCreateTaskCommand()
    val tc = new RenameTaskCommand(COMMAND_ID_TWO, TASK_ID_ZERO, task.version + 1, "change the description")
    an[IllegalArgumentException] should be thrownBy task.handle(tc)
  }

  /*
  "Handling events " should " check for the correct event versions" in {
    val task = freshTask()
    val createEventWithTooHighVersion = new TaskCreatedEvent(EVENT_ID_ONE, task.id, task.version + 1, task.version + 2, "new desc")

    an[IllegalArgumentException] should be thrownBy task.applyEvent(createEventWithTooHighVersion)
  }
  */
}
