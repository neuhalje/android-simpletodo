package name.neuhalfen.myscala.domain.application

import name.neuhalfen.myscala.test.UnitSpec
import name.neuhalfen.myscala.domain.model._
import name.neuhalfen.myscala.domain.model.CreateTaskCommand

class TaskManagingApplicationTest extends UnitSpec {

  "The task application service " should " return None for a non existing task" in {
    val tasksApp = new TaskManagingApplication
    tasksApp.loadTask(UnitSpec.TASK_ID_NON_EXISTING) should be('empty)
  }

  it should " return the task for an existing task" in {
    val tasksApp = new TaskManagingApplication
    val createTaskCommand: CreateTaskCommand = Commands.createTask("task")
    tasksApp.executeCommand(createTaskCommand)

    tasksApp.loadTask(createTaskCommand.aggregateRootId) should be('defined)
  }

  it should " update a task " in {
    val tasksApp = new TaskManagingApplication

    val createTaskCommand: CreateTaskCommand = Commands.createTask("task")
    tasksApp.executeCommand(createTaskCommand)

    val renameTaskCommand = Commands.renameTask(tasksApp.loadTask(createTaskCommand.aggregateRootId).get, "renamed task")
    tasksApp.executeCommand(renameTaskCommand)

    tasksApp.loadTask(createTaskCommand.aggregateRootId).get.taskSummary should be(TaskSummary(renameTaskCommand.aggregateRootId, renameTaskCommand.aggregateRootVersion + 1, "renamed task"))
  }

  it should "fail, when the command targets the wrong version" in {
    val tasksApp = new TaskManagingApplication

    val createTaskCommand: CreateTaskCommand = Commands.createTask("task")
    tasksApp.executeCommand(createTaskCommand)

    val renameTaskCommandWithWrongVersion = RenameTaskCommand(UnitSpec.COMMAND_ID_TWO, createTaskCommand.aggregateRootId, 999, "renamed task")
    an[IllegalArgumentException] should be thrownBy tasksApp.executeCommand(renameTaskCommandWithWrongVersion)
  }
}