package name.neuhalfen.myscala.domain.infrastructure

import name.neuhalfen.myscala.domain.model.Event

class DummyEventPublisher extends EventPublisher {
  def publishEventsInTransaction(events: java.util.List[Event]): Unit = {}

  /**
   * The events are committed to the database, the transaction is done.
   *
   * The object implementing this method can use the events to e.g. inform the user.
   *
   * @param events
   */
  def publishEventsAfterCommit(events: java.util.List[Event]): Unit = {}


}
