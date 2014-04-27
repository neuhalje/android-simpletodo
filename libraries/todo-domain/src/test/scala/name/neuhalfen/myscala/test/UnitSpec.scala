package name.neuhalfen.myscala.test

import org.scalatest._
import java.util.UUID


object UnitSpec {

  val TASK_ID_NON_EXISTING = UUID.fromString("ffffffff-aaaa-ffff-ffff-ffffffffffff")

  val TASK_ID_ZERO = UUID.fromString("00000000-aaaa-0000-0000-000000000000")
  val TASK_ID_ONE = UUID.fromString("11111111-aaaa-1111-1111-111111111111")

  val COMMAND_ID_ZERO = UUID.fromString("00000000-cccc-0000-0000-000000000000")
  val COMMAND_ID_ONE = UUID.fromString("11111111-cccc-1111-1111-111111111111")
  val COMMAND_ID_TWO = UUID.fromString("22222222-cccc-2222-2222-222222222222")
  val COMMAND_ID_THREE = UUID.fromString("33333333-cccc-3333-3333-333333333333")

  val EVENT_ID_ZERO = UUID.fromString("00000000-eeee-0000-0000-000000000000")
  val EVENT_ID_ONE = UUID.fromString("11111111-eeee-1111-1111-111111111111")
}

/**
 * Baseclass for all unit tests.
 */
abstract class UnitSpec extends FlatSpec with Matchers with
OptionValues with Inside with Inspectors
{

}
