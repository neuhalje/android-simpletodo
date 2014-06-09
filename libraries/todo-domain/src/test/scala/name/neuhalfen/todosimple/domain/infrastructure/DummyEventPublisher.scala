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
package name.neuhalfen.todosimple.domain.infrastructure


import name.neuhalfen.todosimple.domain.model.Event

class DummyEventPublisher[ENTITY] extends EventPublisher[ENTITY] {
  def publishEventsInTransaction(events: Seq[Event[ENTITY]]): Unit = {}

  /**
   * The events are committed to the database, the transaction is done.
   *
   * The object implementing this method can use the events to e.g. inform the user.
   *
   * @param events
   */
  def publishEventsAfterCommit(events: Seq[Event[ENTITY]]): Unit = {}


}
