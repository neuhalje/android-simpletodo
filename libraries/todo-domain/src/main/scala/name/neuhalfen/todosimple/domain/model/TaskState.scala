package name.neuhalfen.todosimple.domain.model

/**
 * Created by jens on 03/05/14.
 */
object TaskState extends Enumeration {
  type TaskState = Value
  val NOT_CREATED, CREATED, DELETED = Value
}
