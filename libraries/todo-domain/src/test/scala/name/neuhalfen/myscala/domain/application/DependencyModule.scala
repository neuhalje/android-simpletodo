package name.neuhalfen.myscala.domain.application

import name.neuhalfen.myscala.domain.infrastructure.MemoryEventStore
import com.google.inject.Module
import com.google.inject.Binder


class DependencyModule extends Module {
  def configure(binder: Binder) = {
    binder.bind(classOf[EventStore]).to(classOf[MemoryEventStore])
  }
}

