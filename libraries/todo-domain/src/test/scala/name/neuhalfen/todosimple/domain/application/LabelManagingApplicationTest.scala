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
package name.neuhalfen.todosimple.domain.application

import com.google.inject.Inject
import name.neuhalfen.todosimple.test.UnitSpec
import scala.annotation.meta.field
import name.neuhalfen.todosimple.domain.model.{RenameLabelCommand, Commands, CreateLabelCommand}

class LabelManagingApplicationTest extends UnitSpec {
  @(Inject@field)
  var labelApp: LabelManagingApplication = _

  "The task application service " should " return None for a non existing task" in {
    labelApp.loadEntity(UnitSpec.LABEL_ID_NON_EXISTING) should be('empty)
  }

  it should " return the task for an existing task" in {
    val createLabelCommand: CreateLabelCommand = Commands.createLabel("task title")
    labelApp.executeCommand(createLabelCommand)

    labelApp.loadEntity(createLabelCommand.aggregateRootId) should be('defined)
  }


  it should " update a task " in {

    val createLabelCommand: CreateLabelCommand = Commands.createLabel("label title")
    labelApp.executeCommand(createLabelCommand)

    val renameLabelCommand = Commands.renameLabel(labelApp.loadEntity(createLabelCommand.aggregateRootId).get, "renamed label:title")
    labelApp.executeCommand(renameLabelCommand)

    val t = labelApp.loadEntity(createLabelCommand.aggregateRootId).get
    t.title should be("renamed label:title")
    t.version should be(renameLabelCommand.aggregateRootVersion + 1)
  }

  it should "fail, when the command targets the wrong version" in {

    val createLabelCommand: CreateLabelCommand = Commands.createLabel("task title")
    labelApp.executeCommand(createLabelCommand)

    val renameLabelCommandWithWrongVersion = RenameLabelCommand(UnitSpec.LABEL_COMMAND_ID_TWO, createLabelCommand.aggregateRootId, 999, "renamed label")
    an[IllegalArgumentException] should be thrownBy labelApp.executeCommand(renameLabelCommandWithWrongVersion)
  }

  it should "return tasks without uncomitted events" in {

    val createLabelCommand: CreateLabelCommand = Commands.createLabel("task title")
    labelApp.executeCommand(createLabelCommand)

    val task =  labelApp.loadEntity(createLabelCommand.aggregateRootId).get
    task.uncommittedEVTs should be (empty)
  }
}
