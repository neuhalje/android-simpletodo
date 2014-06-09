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
package name.neuhalfen.todosimple.test

import org.scalatest._
import com.google.inject.Injector
import name.neuhalfen.todosimple.test.di.ServiceInjector
import name.neuhalfen.todosimple.domain.model._
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat


object UnitSpec {

  val TASK_ID_NON_EXISTING = TaskId("ffffffff-aaaa-ffff-ffff-ffffffffffff")

  val TASK_ID_ZERO = TaskId("00000000-aaaa-0000-0000-000000000000")
  val TASK_ID_ONE = TaskId("11111111-aaaa-1111-1111-111111111111")

  val TASK_COMMAND_ID_ZERO = CommandId[Task]("00000000-cccc-0000-0000-000000000000")
  val TASK_COMMAND_ID_ONE = CommandId[Task]("11111111-cccc-1111-1111-111111111111")
  val TASK_COMMAND_ID_TWO = CommandId[Task]("22222222-cccc-2222-2222-222222222222")
  val TASK_COMMAND_ID_THREE = CommandId[Task]("33333333-cccc-3333-3333-333333333333")

  val TASK_EVENT_ID_ZERO = EventId[Task]("00000000-eeee-0000-0000-000000000000")
  val TASK_EVENT_ID_ONE = EventId[Task]("11111111-eeee-1111-1111-111111111111")


  val LABEL_ID_NON_EXISTING = LabelId("ffffffff-2222-2222-ffff-ffffffffffff")
  val LABEL_ID_ZERO = LabelId("00000000-0000-2222-0000-000000000000")
  val LABEL_ID_ONE = LabelId("11111111-1111-2222-1111-111111111111")
  val LABEL_ID_TWO = LabelId("11111111-2222-2222-1111-111111111111")
  val LABEL_COMMAND_ID_ZERO = CommandId[Label]("00000000-cccc-2222-0000-000000000000")
  val LABEL_COMMAND_ID_ONE = CommandId[Label]("11111111-cccc-2222-0000-111111111111")
  val LABEL_COMMAND_ID_TWO = CommandId[Label]("22222222-cccc-2222-0000-111111111111")
  val LABEL_EVENT_ID_ZERO = EventId[Label]("00000000-eeee-2222-0000-000000000000")
  val LABEL_EVENT_ID_ONE = EventId[Label]("11111111-eeee-2222-1111-111111111111")

  val TIME_NOW = DateTime.parse("20140517T165800Z", ISODateTimeFormat.basicDateTimeNoMillis())
  val TIME_BEFORE = DateTime.parse("19990517T165800Z", ISODateTimeFormat.basicDateTimeNoMillis())
  val TIME_AFTER = DateTime.parse("30140517T165800Z", ISODateTimeFormat.basicDateTimeNoMillis())
}

/**
 * Baseclass for all unit tests.
 * Supports @Inject. Injected vars are recreated for each test method
 */
abstract class UnitSpec extends FlatSpec with Matchers with
OptionValues with Inside with Inspectors with BeforeAndAfter {
  var injector: Injector = _

  before {

    injector = ServiceInjector.injector
    injector.injectMembers(this)
  }

}
