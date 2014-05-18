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
package name.neuhalfen.todosimple.domain.model

import org.joda.time.DateTime


sealed trait Event {
  val id: EventId
  val aggregateRootId: TaskId
  val originalAggregateRootVersion: Int
  val newAggregateRootVersion: Int
  val occurredAt: DateTime

  override def toString: String = s"${getClass.getSimpleName}: ${id.toString}. Aggregate: ${aggregateRootId.toString} v$originalAggregateRootVersion->v$newAggregateRootVersion"
}

case class TaskCreatedEvent(id: EventId, aggregateRootId: TaskId, originalAggregateRootVersion: Int, newAggregateRootVersion: Int, occurredAt: DateTime, title: String, description: String) extends Event {
  override def toString: String = super.toString() + s", title:'$title', description: '$description'"
}

case class TaskRenamedEvent(id: EventId, aggregateRootId: TaskId, originalAggregateRootVersion: Int, newAggregateRootVersion: Int, occurredAt: DateTime, newTitle: String, newDescription: String) extends Event {
  override def toString: String = super.toString() + s",  newTitle: '$newTitle', newDescription: '$newDescription'"
}

case class TaskDeletedEvent(id: EventId, aggregateRootId: TaskId, originalAggregateRootVersion: Int, newAggregateRootVersion: Int, occurredAt: DateTime) extends Event {
  override def toString: String = super.toString()
}



