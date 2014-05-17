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
import _root_.name.neuhalfen.todosimple.test.UnitSpec._

/**
 * Some generic tests to see, if event and command handling works properly
 */
class TaskBasicCommandAndEventHandlingTest extends UnitSpec with TaskTestTrait {


  "A task " should " report its id" in {
    val task = createUncommitedTaskViaCreateTaskCommand()
    assert(task.id == TASK_ID_ONE)
  }

  it should "have a toString that includes the id" in {
    val task = createUncommitedTaskViaCreateTaskCommand()
    task.toString() should include(task.id.toString())
  }

  "Running a command " should " return only a Events with the correct aggregate" in {
    var task = createUncommitedTaskViaCreateTaskCommand()
    val tc = new RenameTaskCommand(COMMAND_ID_TWO, task.id, task.version, "change the title", "change the description")
    task = task.handle(tc)
    for (evt <- task.uncommittedEVTs) {
      assert(evt.aggregateRootId == task.id)
    }
  }

  it should " return events with ascending versions " in {
    var task = createUncommitedTaskViaCreateTaskCommand()
    val tc = new RenameTaskCommand(COMMAND_ID_TWO, task.id, task.version, "change the title", "change the description")

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
    val task = createUncommitedTaskViaCreateTaskCommand()
    val tc = new RenameTaskCommand(COMMAND_ID_TWO, task.id, task.version + 1, "change the title", "change the description")
    an[IllegalArgumentException] should be thrownBy task.handle(tc)
  }



  it should "fail, if the the aggregate does not match the command" in {
    val task = createUncommitedTaskViaCreateTaskCommand()
    val tc = new RenameTaskCommand(COMMAND_ID_TWO, TASK_ID_ZERO, task.version + 1, "change the title", "change the description")
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
