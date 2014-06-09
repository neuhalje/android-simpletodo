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
package name.neuhalfen.todosimple.domain.model

import _root_.name.neuhalfen.todosimple.test.UnitSpec

trait TaskTestTrait {


  def createUncommitedTaskViaCreateTaskCommand(): Task = {
    Task.newTask(new CreateTaskCommand(UnitSpec.TASK_COMMAND_ID_ONE, UnitSpec.TASK_ID_ONE, "fresh title", "fresh desc"))
  }

  def loadExistingTask(): Task = {
    Task.loadFromHistory(List(new TaskCreatedEvent(UnitSpec.TASK_EVENT_ID_ZERO, UnitSpec.TASK_ID_ONE, 0, 1, UnitSpec.TIME_BEFORE, "loaded title", "loaded desc")))
  }
}
