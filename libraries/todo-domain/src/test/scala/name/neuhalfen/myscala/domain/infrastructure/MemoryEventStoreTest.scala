package name.neuhalfen.myscala.domain.infrastructure

import name.neuhalfen.myscala.test.UnitSpec
import name.neuhalfen.myscala.domain.model.{TaskRenamedEvent, TaskCreatedEvent}
import name.neuhalfen.myscala.domain.model.Event
import java.util.UUID
import name.neuhalfen.myscala.domain.infrastructure.impl.MemoryEventStore

class MemoryEventStoreTest extends UnitSpec {

  def newStore = new MemoryEventStore

  val taskId: UUID = UnitSpec.TASK_ID_ONE


  "Reading events for a non existing aggregate " should " return None " in {
    assert(newStore.loadEvents(UnitSpec.TASK_ID_NON_EXISTING).isEmpty)
  }

  "Writing events for a new aggregate " should " save them" in {
    val store = newStore

    val taskCreatedEvent: TaskCreatedEvent = TaskCreatedEvent(UnitSpec.EVENT_ID_ONE, taskId, 0, 1, "new task")
    val taskRenamedEvent: TaskRenamedEvent = TaskRenamedEvent(UnitSpec.EVENT_ID_ONE, taskId, 1, 2, "renamed task")

    val events: List[Event] = List(taskCreatedEvent, taskRenamedEvent)
    store.appendEvents(taskId, events)

    val loadedEvents = store.loadEvents(taskId)
    assert(loadedEvents.isDefined)

    loadedEvents.get should contain theSameElementsInOrderAs events
  }

  "Writing events for an existing aggregate " should " append them" in {
    val store = newStore

    val taskCreatedEvent: TaskCreatedEvent = TaskCreatedEvent(UnitSpec.EVENT_ID_ONE, taskId, 0, 1, "new task")
    val taskRenamedEvent: TaskRenamedEvent = TaskRenamedEvent(UnitSpec.EVENT_ID_ONE, taskId, 1, 2, "renamed task")

    store.appendEvents(taskId, List(taskCreatedEvent))
    store.appendEvents(taskId, List(taskRenamedEvent))

    val loadedEvents = store.loadEvents(taskId)

    loadedEvents.get should contain theSameElementsInOrderAs List(taskCreatedEvent, taskRenamedEvent)
  }
}
