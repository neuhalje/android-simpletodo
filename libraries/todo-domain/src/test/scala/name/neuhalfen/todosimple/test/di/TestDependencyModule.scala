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
package name.neuhalfen.todosimple.test.di

import com.google.inject.{Provides, Module, Binder}
import name.neuhalfen.todosimple.domain.application.Cache
import name.neuhalfen.todosimple.domain.infrastructure._
import name.neuhalfen.todosimple.domain.infrastructure.impl.MemoryEventStore
import name.neuhalfen.todosimple.domain.model.{Label, Task}


class TestDependencyModule extends Module {
  def configure(binder: Binder) = {
    binder.bind(classOf[Transaction]).to(classOf[DummyTransaction])
  }

  @Provides
  def provideTaskCache() : Cache[Task] =  new InMemoryCache[Task]

  @Provides
  def provideTaskEventPublisher() : EventPublisher[Task] =  new DummyEventPublisher[Task]

  @Provides
  def provideTaskEventStore() : EventStore[Task] =  new MemoryEventStore[Task]
  /// Label
  @Provides
  def provideLabelCache() : Cache[Label] =  new InMemoryCache[Label]

  @Provides
  def provideLabelEventPublisher() : EventPublisher[Label] =  new DummyEventPublisher[Label]

  @Provides
  def provideLabelEventStore() : EventStore[Label] =  new MemoryEventStore[Label]
}

