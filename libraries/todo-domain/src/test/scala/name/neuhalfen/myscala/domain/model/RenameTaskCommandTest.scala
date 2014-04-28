package name.neuhalfen.myscala.domain.model

import _root_.name.neuhalfen.myscala.test.UnitSpec

class RenameTaskCommandTest extends UnitSpec with TaskTestTrait {

  "Renaming a loaded task " should " return (only) a TaskRenamedEvent with the correct description" in {
    val task = loadFreshTask()

    task.handle(Commands.renameTask(task,"new description"))
    for (evt <- task.uncommittedEVTs) evt match {
      case TaskRenamedEvent(eventId, aggregateRootId, oldAggregateVersion, newAggregateVersion, newDescription) => assert(newDescription == "new description")
      case _ => fail("Unecpected event")
    }
  }

  "Renaming a freshly created task " should " return a Created, and a Renamed event with the correct description" in {
    val task = loadFreshTask()

    // TODO: This does not test the order of events
    task.handle(Commands.renameTask(task,"new description"))
    for (evt <- task.uncommittedEVTs) evt match {
      case TaskCreatedEvent(eventId, aggregateRootId, oldAggregateVersion, newAggregateVersion, newDescription) => assert(newDescription == "loaded")
      case TaskRenamedEvent(eventId, aggregateRootId, oldAggregateVersion, newAggregateVersion, newDescription) => assert(newDescription == "new description")
    }
  }

}
