package name.neuhalfen.myscala.domain.infrastructure

import name.neuhalfen.myscala.domain.model.Event



/**
 * When an application transaction is about to be complete,  the events need to be published
 */
trait EventPublisher {
  /**
   * The events are not yet committed to the database, the transaction is still ongoing.
   *
   * The object implementing this method should save the events to the database
   *
   * @param events
   */
  def publishEventsInTransaction(events: java.util.List[Event]): Unit

  /**
   * The events are committed to the database, the transaction is done.
   *
   * The object implementing this method can use the events to e.g. inform the user.
   *
   * @param events
   */
  @Deprecated
  def publishEventsAfterCommit(events: java.util.List[Event]): Unit
}
