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
import name.neuhalfen.todosimple.domain.model.{EventId, CommandId, TaskId}


object UnitSpec {

  val TASK_ID_NON_EXISTING = new TaskId("ffffffff-aaaa-ffff-ffff-ffffffffffff")

  val TASK_ID_ZERO = new TaskId("00000000-aaaa-0000-0000-000000000000")
  val TASK_ID_ONE = new TaskId("11111111-aaaa-1111-1111-111111111111")

  val COMMAND_ID_ZERO = new CommandId("00000000-cccc-0000-0000-000000000000")
  val COMMAND_ID_ONE = new CommandId("11111111-cccc-1111-1111-111111111111")
  val COMMAND_ID_TWO = new CommandId("22222222-cccc-2222-2222-222222222222")
  val COMMAND_ID_THREE = new CommandId("33333333-cccc-3333-3333-333333333333")

  val EVENT_ID_ZERO = new EventId("00000000-eeee-0000-0000-000000000000")
  val EVENT_ID_ONE = new EventId("11111111-eeee-1111-1111-111111111111")
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
