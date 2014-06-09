package name.neuhalfen.todosimple.domain.model
import java.util.UUID

case class LabelId(id: UUID) extends UniqueId[Label](id)

object LabelId {
  def generateId(): LabelId = new LabelId(UUID.randomUUID())

  def fromString(s: String): LabelId = new LabelId(UUID.fromString( s))

  def apply(s:String) : LabelId = fromString(s)
}

