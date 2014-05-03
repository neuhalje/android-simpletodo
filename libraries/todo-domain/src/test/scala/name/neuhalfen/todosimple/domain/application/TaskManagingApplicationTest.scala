package name.neuhalfen.todosimple.domain.application

import name.neuhalfen.todosimple.test.UnitSpec
import name.neuhalfen.todosimple.domain.model._
import name.neuhalfen.todosimple.domain.model.CreateTaskCommand
import com.google.inject.Inject
import scala.annotation.meta.field

class TaskManagingApplicationTest extends UnitSpec {
  @(Inject @field)
  var tasksApp : TaskManagingApplication = _

  "The task application service " should " return None for a non existing task" in {
    tasksApp.loadTask(UnitSpec.TASK_ID_NON_EXISTING) should be('empty)
  }

  it should " return the task for an existing task" in {
    val createTaskCommand: CreateTaskCommand = Commands.createTask("task")
    tasksApp.executeCommand(createTaskCommand)

    tasksApp.loadTask(createTaskCommand.aggregateRootId) should be('defined)
  }

  it should " update a task " in {

    val createTaskCommand: CreateTaskCommand = Commands.createTask("task")
    tasksApp.executeCommand(createTaskCommand)

    val renameTaskCommand = Commands.renameTask(tasksApp.loadTask(createTaskCommand.aggregateRootId).get, "renamed task")
    tasksApp.executeCommand(renameTaskCommand)

    val t = tasksApp.loadTask(createTaskCommand.aggregateRootId).get
    t._description should be("renamed task")
    t.version should be(renameTaskCommand.aggregateRootVersion + 1)
  }

  it should "fail, when the command targets the wrong version" in {

    val createTaskCommand: CreateTaskCommand = Commands.createTask("task")
    tasksApp.executeCommand(createTaskCommand)

    val renameTaskCommandWithWrongVersion = RenameTaskCommand(UnitSpec.COMMAND_ID_TWO, createTaskCommand.aggregateRootId, 999, "renamed task")
    an[IllegalArgumentException] should be thrownBy tasksApp.executeCommand(renameTaskCommandWithWrongVersion)
  }
}
