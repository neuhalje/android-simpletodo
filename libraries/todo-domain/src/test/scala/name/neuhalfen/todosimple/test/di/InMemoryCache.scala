package name.neuhalfen.todosimple.test.di

import name.neuhalfen.todosimple.domain.application.Cache
import name.neuhalfen.todosimple.domain.model.{Task, TaskId}


object InMemoryCache extends Cache {
  private val cache: scala.collection.mutable.Map[TaskId, Task] = scala.collection.mutable.Map.empty

  @Override
  def put(aggregate: Task): Unit = {
    cache.put(aggregate.id, aggregate)
  }

  @Override
  def get(aggregateId: TaskId): Option[Task] = {
    cache.get(aggregateId)
  }

}

