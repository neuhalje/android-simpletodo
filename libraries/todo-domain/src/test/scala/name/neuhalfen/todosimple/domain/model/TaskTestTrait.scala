package name.neuhalfen.todosimple.domain.model

import _root_.name.neuhalfen.todosimple.test.UnitSpec

trait TaskTestTrait {


  def createUncommitedTaskViaCreateTaskCommand(): Task = {
    Task.newTask(new CreateTaskCommand(UnitSpec.COMMAND_ID_ONE, UnitSpec.TASK_ID_ONE, "fresh"))
  }

  def loadExistingTask(): Task = {
    Task.loadFromHistory(List(new TaskCreatedEvent(UnitSpec.EVENT_ID_ZERO, UnitSpec.TASK_ID_ONE, 0, 1, "loaded")))
  }
}
