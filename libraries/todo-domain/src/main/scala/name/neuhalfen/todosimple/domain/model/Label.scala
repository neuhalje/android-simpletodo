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

import name.neuhalfen.todosimple.domain.model.EntityState.EntityState
import org.joda.time.DateTime


object Label extends AggregateFactory[Label, Event[Label]] {


  override def applyEvent = {
    case event: LabelCreatedEvent => Label(event.aggregateRootId, event.newAggregateRootVersion, event :: Nil, event.title,  EntityState.CREATED)
    case event => unhandled(event)
  }

  def newLabel(command: CreateLabelCommand): Label = {
    applyEvent(new LabelCreatedEvent(EventId.generateId(), command.aggregateRootId, 0, 1, DateTime.now(), command.title))
  }

  override def newInstance = new Label(null, 0, List[Event[Label]](), "",  EntityState.NOT_CREATED)
}

case class Label(
                  _aggregateId: LabelId,
                  _version: Int,
                  _uncommittedEvents: List[Event[Label]],
                  title: String,
                  state: EntityState
                  ) extends AggregateRoot[Label, Event[Label]] {

  def id = _aggregateId

  def version = _version

  def handle(command: Command[Label]): Label = {
    command match {
      case c: CreateLabelCommand => createLabel(c)
      case c: RenameLabelCommand => renameLabel(c)
      case c: DeleteLabelCommand => deleteLabel(c)
    }
  }

  private def createLabel(command: CreateLabelCommand): Label = {
    requireCorrectAggregateVersion(command.aggregateRootVersion)
    requireState(EntityState.NOT_CREATED)

    applyEvent(new LabelCreatedEvent(EventId.generateId(), command.aggregateRootId, 0, 1, DateTime.now(), command.title))
  }

  /**
   * Delete is idempotent, deleting deleted labels does noting
   * @param command
   * @return
   */
  private def deleteLabel(command: DeleteLabelCommand): Label = {
    requireCorrectAggregateId(command.aggregateRootId)
    requireCorrectAggregateVersion(command.aggregateRootVersion)

    if (EntityState.DELETED == state) {
      this
    } else {
      requireState(EntityState.CREATED)
      applyEvent(new LabelDeletedEvent(EventId.generateId(), id, version, version + 1, DateTime.now()))
    }
  }

  private def renameLabel(command: RenameLabelCommand): Label = {
    requireCorrectAggregateId(command.aggregateRootId)
    requireCorrectAggregateVersion(command.aggregateRootVersion)
    requireState(EntityState.CREATED)

    if (title.equals(command.newTitle)) {
      this
    } else {
      applyEvent(new LabelRenamedEvent(EventId.generateId(), id, version, version + 1, DateTime.now(), command.newTitle))
    }
  }

  override def applyEvent = {
    case event: Event[Label] => {
      // The guard needs to let through "label created" events.
      val labelIsNew = (id == null)
      require(event.aggregateRootId == id || labelIsNew, s"wrong aggregate root '${event.aggregateRootId}', should be '$id'")
      require(event.originalAggregateRootVersion == version, s"wrong aggregate version ${event.originalAggregateRootVersion}, should be $version")

      event match {
        case LabelCreatedEvent(_, aggregateRootId, _, newAggregateVersion, _, newTitle) =>
          copy(aggregateRootId, newAggregateVersion, _uncommittedEvents :+ event, newTitle, EntityState.CREATED)

        case LabelRenamedEvent(_, _, _, newAggregateVersion, _, newTitle) =>
          copy(id, newAggregateVersion, _uncommittedEvents :+ event, newTitle,  state)

        case LabelDeletedEvent(_, _, _, newAggregateVersion, _) =>
          copy(id, newAggregateVersion, _uncommittedEvents :+ event, title, EntityState.DELETED)
      }
    }
  }

  override def toString: String = s"$id@v${_version}: '${title}'"

  override def markCommitted: Label = copy(_uncommittedEvents = Nil)

  override def uncommittedEVTs: Seq[Event[Label]] = _uncommittedEvents

  protected def requireState(requiredState: EntityState) {
    require(state == requiredState, s"State of label ${_aggregateId} v$version must be $requiredState but is $state")
  }

  protected def requireCorrectAggregateId(aggregateRootIdFromExternal: LabelId) {
    require(id == aggregateRootIdFromExternal, s"wrong aggregate root '$aggregateRootIdFromExternal', should be '$id'")
  }

  protected def requireCorrectAggregateVersion(versionFromExternal: Int) {
    require(versionFromExternal == version, s"wrong aggregate version $versionFromExternal, should be $version")
  }
}
