package name.neuhalfen.todosimple.android.infrastructure.cache;

import name.neuhalfen.todosimple.domain.application.Cache;
import name.neuhalfen.todosimple.domain.model.Task;
import name.neuhalfen.todosimple.domain.model.UniqueId;
import scala.Option;
import scala.Some;

import java.util.HashMap;
import java.util.Map;

public class TaskCache implements Cache<Task> {
    private Map<UniqueId<Task>, Task> cache = new HashMap<UniqueId<Task>, Task>();

    private int hits = 0;
    private int misses = 0;

    public int getMisses() {
        return misses;
    }

    public int getHits() {
        return hits;
    }

    public int getSize() {
        return cache.size();
    }


    @Override
    public void put(Task aggregate) {
        cache.put(aggregate.id(), aggregate);
    }

    @Override
    public Option<Task> get(UniqueId<Task> aggregateId) {
        final Task t = cache.get(aggregateId);

        if (null == t) {
            misses++;
            return Option.empty();
        } else {
            hits++;
            return new Some<Task>(t);
        }
    }
}
