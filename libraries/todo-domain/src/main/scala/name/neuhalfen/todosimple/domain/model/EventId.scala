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

import java.util.UUID

case class EventId[ENTITY](id: UUID) extends UniqueId[Event[ENTITY]](id)

object EventId {
  def  generateId[ENTITY](): EventId[ENTITY] = new EventId[ENTITY](UUID.randomUUID())

  def fromString[ENTITY](s: String): EventId[ENTITY] = new EventId[ENTITY](UUID.fromString( s))
  def apply[ENTITY](s:String) : EventId[ENTITY] = fromString(s)
}

