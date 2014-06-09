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

case class CommandId[ENTITY](id: UUID) extends UniqueId[Command[ENTITY]](id)

object CommandId {
  def generateId[ENTITY](): CommandId[ENTITY] = new CommandId[ENTITY](UUID.randomUUID())

  def fromString[ENTITY](s: String): CommandId[ENTITY] = new CommandId[ENTITY](UUID.fromString( s))
  def apply[ENTITY](s:String) : CommandId[ENTITY] = fromString(s)
}

