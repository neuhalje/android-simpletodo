/*
Copyright 2014 Jens Neuhalfen

Licensed under the Apache License, Version 2.0 (the "License"); you may not use
this file except in compliance with the License. You may obtain a copy of the
License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed
under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
 */
package name.neuhalfen.todosimple.domain.application

import name.neuhalfen.todosimple.test.UnitSpec
import name.neuhalfen.todosimple.domain.model._
import name.neuhalfen.todosimple.domain.model.CreateTaskCommand
import com.google.inject.Inject
import scala.annotation.meta.field

class TaskManagingApplicationTest extends UnitSpec {
  @(Inject@field)
  var tasksApp: TaskManagingApplication = _

  "The task application service " should " return None for a non existing task" in {
    tasksApp.loadEntity(UnitSpec.TASK_ID_NON_EXISTING) should be('empty)
  }

  it should " return the task for an existing task" in {
    val createTaskCommand: CreateTaskCommand = Commands.createTask("task title", "task desc")
    tasksApp.executeCommand(createTaskCommand)

    tasksApp.loadEntity(createTaskCommand.aggregateRootId) should be('defined)
  }


  it should " update a task " in {

    val createTaskCommand: CreateTaskCommand = Commands.createTask("task title", "task desc")
    tasksApp.executeCommand(createTaskCommand)

    val renameTaskCommand = Commands.renameTask(tasksApp.loadEntity(createTaskCommand.aggregateRootId).get, "renamed task:title", "renamed task:desc")
    tasksApp.executeCommand(renameTaskCommand)

    val t = tasksApp.loadEntity(createTaskCommand.aggregateRootId).get
    t._title should be("renamed task:title")
    t._description should be("renamed task:desc")
    t.version should be(renameTaskCommand.aggregateRootVersion + 1)
  }

  it should "fail, when the command targets the wrong version" in {

    val createTaskCommand: CreateTaskCommand = Commands.createTask("task title", "task desc")
    tasksApp.executeCommand(createTaskCommand)

    val renameTaskCommandWithWrongVersion = RenameTaskCommand(UnitSpec.TASK_COMMAND_ID_TWO, createTaskCommand.aggregateRootId, 999, "renamed task", "xx")
    an[IllegalArgumentException] should be thrownBy tasksApp.executeCommand(renameTaskCommandWithWrongVersion)
  }

  it should "return tasks without uncomitted events" in {

    val createTaskCommand: CreateTaskCommand = Commands.createTask("task title", "task desc")
    tasksApp.executeCommand(createTaskCommand)

    val task =  tasksApp.loadEntity(createTaskCommand.aggregateRootId).get
    task.uncommittedEVTs should be (empty)
  }
}
