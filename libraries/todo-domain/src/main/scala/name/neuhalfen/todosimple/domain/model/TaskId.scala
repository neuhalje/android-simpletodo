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

case class TaskId(id: UUID) {
  if (null == id) throw new IllegalArgumentException("id must not be NULL")

  def this() = this(UUID.randomUUID())

  def this(idstr: String) = this(UUID.fromString(idstr))

  override def toString: String = s"${id.toString}"
}

object TaskId {
  def generateId(): TaskId = new TaskId()

  def fromString(s: String): TaskId = new TaskId(s)
}
