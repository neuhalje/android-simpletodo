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
package name.neuhalfen.todosimple.domain.infrastructure.impl

import name.neuhalfen.todosimple.domain.model.{TaskId, Event}
import name.neuhalfen.todosimple.domain.infrastructure.EventStore

class MemoryEventStore extends EventStore {
  val data = collection.mutable.Map[TaskId, List[Event]]()

  override def appendEvents(aggregateId: TaskId, events: Seq[Event]): Unit = {
    val stored = data getOrElse(aggregateId, List[Event]())
    data(aggregateId) = stored ++ events
  }

  override def loadEvents(aggregateId: TaskId): Option[Seq[Event]] = {
    data get aggregateId
  }
}
