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

class LabelTaskCommandTest extends UnitSpec with TaskTestTrait {

  "Labeling  a loaded task " should " return (only) a TaskLabeledEvent with the correct label " in {
    val originalTask = loadExistingTask()

    val task = originalTask.handle(Commands.labelTask(originalTask, UnitSpec.LABEL_ID_ZERO))
    assert(task.uncommittedEVTs.size == 1)

    for (evt <- task.uncommittedEVTs) evt match {
      case TaskLabeledEvent(eventId, aggregateRootId, oldAggregateVersion, newAggregateVersion, _, newLabel) => assert(newLabel == UnitSpec.LABEL_ID_ZERO)
      case _ => fail("Unexpected event")
    }
  }

  "Labeling  a loaded task " should " increment the task version" in {
    val originalTask = loadExistingTask()
    val originalVersion = originalTask.version

    val task = originalTask.handle(Commands.labelTask(originalTask, UnitSpec.LABEL_ID_ZERO))
    assert(task.version > originalVersion)
  }

  it should " not modify the task state" in {
    val originalTask = loadExistingTask()
    val task = originalTask.handle(Commands.labelTask(originalTask, UnitSpec.LABEL_ID_ZERO))
    assert(task.state == EntityState.CREATED)
  }

  it should " not modify the task version when the same label is applied twice" in {
    val originalTask = loadExistingTask()

    val task = originalTask.handle(Commands.labelTask(originalTask, UnitSpec.LABEL_ID_ZERO))
    val task2 = task.handle(Commands.labelTask(task, UnitSpec.LABEL_ID_ZERO))

    assert(task2.version == task.version)
  }
  it should " not modify the task state when the same label is applied twice" in {
    val originalTask = loadExistingTask()

    val task = originalTask.handle(Commands.labelTask(originalTask, UnitSpec.LABEL_ID_ZERO))
    val task2 = task.handle(Commands.labelTask(task, UnitSpec.LABEL_ID_ONE))

    assert(task2.state == EntityState.CREATED)
  }

  "Labeling a freshly created task " should " return a Created, and a Labeled event with the correct label" in {
    val originalTask = createUncommitedTaskViaCreateTaskCommand()

    // TODO: This does not test the order of events
    val task = originalTask.handle(Commands.labelTask(originalTask, UnitSpec.LABEL_ID_ZERO))

    assert(task.uncommittedEVTs.size == 2)
    for (evt <- task.uncommittedEVTs) evt match {
      case TaskCreatedEvent(eventId, aggregateRootId, oldAggregateVersion, newAggregateVersion, newTitle, _, newDescription) => assert(newDescription == "fresh desc")
      case TaskLabeledEvent(eventId, aggregateRootId, oldAggregateVersion, newAggregateVersion, _, newLabel) => assert(newLabel == UnitSpec.LABEL_ID_ZERO)
      case _ => fail("Unexpected event")
    }
  }

  "Assigning an already assigned label " should " not issue an event " in {
    val originalTask = loadExistingTask()
    val taskWithLabel = originalTask.handle(Commands.labelTask(originalTask, UnitSpec.LABEL_ID_ZERO)).markCommitted

    val taskLabeledAgain = taskWithLabel.handle(Commands.labelTask(taskWithLabel, UnitSpec.LABEL_ID_ZERO))

    assert(taskLabeledAgain.uncommittedEVTs.size == 0)
  }

}
