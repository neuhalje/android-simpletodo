package name.neuhalfen.todosimple.test.di

import name.neuhalfen.todosimple.domain.infrastructure._
import com.google.inject.Module
import com.google.inject.Binder
import name.neuhalfen.todosimple.domain.infrastructure.impl.MemoryEventStore


class TestDependencyModule extends Module {
  def configure(binder: Binder) = {
    binder.bind(classOf[EventStore]).to(classOf[MemoryEventStore])
    binder.bind(classOf[EventPublisher]).to(classOf[DummyEventPublisher])
    binder.bind(classOf[Transaction]).to(classOf[DummyTransaction])
  }
}

