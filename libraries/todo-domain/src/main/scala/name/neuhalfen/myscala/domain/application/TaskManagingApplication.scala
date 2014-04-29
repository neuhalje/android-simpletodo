package name.neuhalfen.myscala.domain.application

import name.neuhalfen.myscala.domain.model.Command
import name.neuhalfen.myscala.domain.model.Task
import java.util.UUID
import javax.inject.Inject


class TaskManagingApplication @Inject() (eventStore: EventStore ) {

  def executeCommand(command: Command): Unit = {
    val aggregateId = command.aggregateRootId
    val events = eventStore.loadEvents(aggregateId)

    var task = events match {
      case Some(evts) => Task.loadFromHistory(evts)
      case None => Task.newInstance
    }

    task = task.handle(command)
    eventStore.appendEvents(task.id, task.uncommittedEVTs)
    //task.markCommitted
  }


  def loadTask(taskId: UUID): Option[Task] = {
    val events = eventStore.loadEvents(taskId)
    events match {
      case Some(e) => Some(Task.loadFromHistory(e))
      case None => None
    }

  }
}
