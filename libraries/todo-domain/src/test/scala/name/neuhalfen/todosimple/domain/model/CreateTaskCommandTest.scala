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

class CreateTaskCommandTest extends UnitSpec with TaskTestTrait {

  "Creating a task " should " return only a TaskCreatedEvent with the correct description" in {
    val task = createUncommitedTaskViaCreateTaskCommand()
    assert(task.uncommittedEVTs.size == 1)

    for (evt <- task.uncommittedEVTs) evt match {
      case TaskCreatedEvent(eventId, aggregateRootId, oldAggregateVersion, newAggregateVersion, _, newTitle, newDescription) =>
        assert(newTitle == "fresh title")
        assert(newDescription == "fresh desc")
      case _ => fail("Unexpected event")
    }
  }

  "A create task command " should " base on an aggregate version of zero " in {
    val tc = new CreateTaskCommand(TASK_COMMAND_ID_ONE, TASK_ID_ONE, "title", "description")
    assert(tc.aggregateRootVersion == 0)
  }

  "A created task " should " reject further create commands" in {
    val task = loadExistingTask()
    val tc = new CreateTaskCommand(TASK_COMMAND_ID_TWO, task.id, "title", "description")

    an[IllegalArgumentException] should be thrownBy task.handle(tc)
  }

  it should " be in state CREATED" in {
    val task = createUncommitedTaskViaCreateTaskCommand()
    assert(task.state == EntityState.CREATED)
  }

}
