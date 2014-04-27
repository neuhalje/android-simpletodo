package name.neuhalfen.myscala.domain.model


trait EventSourced[ES <: EventSourced[ES, EVT], EVT] {

  def applyEvent: EVT => ES

  def unhandled(event: EVT): ES = error("event " + event + " does not apply to " + this)
}


trait AggregateRoot[AR <: AggregateRoot[AR, EVT], EVT] extends EventSourced[AR, EVT] {

  def uncommittedEVTs: Seq[EVT]

  def markCommitted: AR
}

trait AggregateFactory[AR <: AggregateRoot[AR, EVT], EVT] extends EventSourced[AR, EVT] {
  def newInstance:  AR

  def loadFromHistory(history: Iterable[EVT]): AR = {
    var aggregate = applyEvent(history.head)
    for (event <- history.tail) {
      aggregate = aggregate.applyEvent(event)
    }
    aggregate.markCommitted
  }
}
