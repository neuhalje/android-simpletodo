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
package name.neuhalfen.todosimple.domain.infrastructure

import name.neuhalfen.todosimple.test.UnitSpec
import name.neuhalfen.todosimple.domain.model.{Task, TaskRenamedEvent, TaskCreatedEvent, Event}
import name.neuhalfen.todosimple.domain.infrastructure.impl.MemoryEventStore

class MemoryEventStoreTest extends UnitSpec {

  def newStore = new MemoryEventStore[Task]

  val taskId = UnitSpec.TASK_ID_ONE


  "Reading events for a non existing aggregate " should " return None " in {
    assert(newStore.loadEvents(UnitSpec.TASK_ID_NON_EXISTING).isEmpty)
  }

  "Writing events for a new aggregate " should " save them" in {
    val store = newStore

    val taskCreatedEvent: TaskCreatedEvent = TaskCreatedEvent(UnitSpec.EVENT_ID_ONE, taskId, 0, 1, UnitSpec.TIME_BEFORE, "new task title", "new task")
    val taskRenamedEvent: TaskRenamedEvent = TaskRenamedEvent(UnitSpec.EVENT_ID_ONE, taskId, 1, 2, UnitSpec.TIME_BEFORE, "renamed task title", "renamed task")

    val events: List[Event[Task]] = List(taskCreatedEvent, taskRenamedEvent)
    store.appendEvents(taskId, events)

    val loadedEvents = store.loadEvents(taskId)
    assert(loadedEvents.isDefined)

    loadedEvents.get should contain theSameElementsInOrderAs events
  }

  "Writing events for an existing aggregate " should " append them" in {
    val store = newStore

    val taskCreatedEvent: TaskCreatedEvent = TaskCreatedEvent(UnitSpec.EVENT_ID_ONE, taskId, 0, 1, UnitSpec.TIME_BEFORE, "new task title", "new task")
    val taskRenamedEvent: TaskRenamedEvent = TaskRenamedEvent(UnitSpec.EVENT_ID_ONE, taskId, 1, 2, UnitSpec.TIME_BEFORE, "renamed task title", "renamed task")

    store.appendEvents(taskId, List(taskCreatedEvent))
    store.appendEvents(taskId, List(taskRenamedEvent))

    val loadedEvents = store.loadEvents(taskId)

    loadedEvents.get should contain theSameElementsInOrderAs List(taskCreatedEvent, taskRenamedEvent)
  }
}
