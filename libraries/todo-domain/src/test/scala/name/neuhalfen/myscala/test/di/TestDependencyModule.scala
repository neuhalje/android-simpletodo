package name.neuhalfen.myscala.test.di

import name.neuhalfen.myscala.domain.infrastructure.MemoryEventStore
import com.google.inject.Module
import com.google.inject.Binder
import name.neuhalfen.myscala.domain.application.EventStore


class TestDependencyModule extends Module {
  def configure(binder: Binder) = {
    binder.bind(classOf[EventStore]).to(classOf[MemoryEventStore])
  }
}

