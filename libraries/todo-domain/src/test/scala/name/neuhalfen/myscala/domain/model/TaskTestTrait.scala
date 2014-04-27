package name.neuhalfen.myscala.domain.model

import _root_.name.neuhalfen.myscala.test.UnitSpec

trait TaskTestTrait {


  def createTaskViaCreateTaskCommand() : Task = {
    Task.newTask(new CreateTaskCommand(UnitSpec.COMMAND_ID_ONE,UnitSpec.TASK_ID_ONE,"fresh"))
  }

  def loadFreshTask() : Task = {
    Task.loadFromHistory(List(new TaskCreatedEvent(UnitSpec.EVENT_ID_ZERO,UnitSpec.TASK_ID_ONE,0,1,"loaded")))
  }
}
