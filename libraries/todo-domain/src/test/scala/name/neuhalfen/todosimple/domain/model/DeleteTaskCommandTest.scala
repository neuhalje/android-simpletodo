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

class DeleteTaskCommandTest extends UnitSpec with TaskTestTrait {

  "deleting a loaded task " should " return (only) a TaskDeletedEvent" in {
    val originalTask = loadExistingTask()

    val task = originalTask.handle(Commands.deleteTask(originalTask))
    assert(task.uncommittedEVTs.size == 1)

    var isFoundDeleted: Boolean = false

    for (evt <- task.uncommittedEVTs) evt match {
      case ev if ev.getClass == classOf[TaskDeletedEvent] => isFoundDeleted = true
      case _ => fail("Unexpected event")
    }

    assert(isFoundDeleted)
  }

  "deleting a freshly created task " should " returns (only) a created, and a deleted event" in {
    val originalTask = createUncommitedTaskViaCreateTaskCommand()

    val task = originalTask.handle(Commands.deleteTask(originalTask))
    assert(task.uncommittedEVTs.size == 2)

    var isFoundCreated: Boolean = false
    var isFoundDeleted: Boolean = false

    for (evt <- task.uncommittedEVTs) evt match {
      case ev if ev.getClass == classOf[TaskCreatedEvent] => isFoundCreated = true
      case ev if ev.getClass == classOf[TaskDeletedEvent] => isFoundDeleted = true
      case _ => fail("Unexpected event")
    }
    assert(isFoundCreated, "Task created event should be found")
    assert(isFoundDeleted, "Task deleted event should be found")
  }

  "Deleting a deleted task " should " be idempotent " in {
    val originalTask = loadExistingTask()

    val deletedTask = originalTask.handle(Commands.deleteTask(originalTask))
    val deletedTwiceTask = deletedTask.handle(Commands.deleteTask(deletedTask))


    assert(deletedTask.uncommittedEVTs.size == deletedTwiceTask.uncommittedEVTs.size)
    assert(deletedTask == deletedTwiceTask)
  }

  "deleting a task" should " change the state to DELETED" in {
    val originalTask = createUncommitedTaskViaCreateTaskCommand()
    assume(originalTask.state == TaskState.CREATED)

    val deletedTask = originalTask.handle(Commands.deleteTask(originalTask))
    assert(deletedTask.state == TaskState.DELETED)
  }

  "Renaming a deleted task " should " fail " in {
    val originalTask = loadExistingTask()
    val deletedTask = originalTask.handle(Commands.deleteTask(originalTask))

    an[IllegalArgumentException] should be thrownBy deletedTask.handle(Commands.renameTask(deletedTask, "die!", "die, die!"))
  }

}
