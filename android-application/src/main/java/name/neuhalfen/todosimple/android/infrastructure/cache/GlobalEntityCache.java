package name.neuhalfen.todosimple.android.infrastructure.cache;

import name.neuhalfen.todosimple.domain.application.Cache;
import name.neuhalfen.todosimple.domain.model.AggregateRoot;
import name.neuhalfen.todosimple.domain.model.Event;
import name.neuhalfen.todosimple.domain.model.UniqueId;
import scala.Option;
import scala.Some;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GlobalEntityCache<T extends AggregateRoot<T,Event<T>>> implements Cache<T> {
    private Map<UniqueId<T>, T> cache = new HashMap<UniqueId<T>, T>();

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
    public void put(T aggregate) {
        cache.put(aggregate.id(), aggregate);
    }

    @Override
    public Option<T> get(UniqueId<T> aggregateId) {
        final T t = cache.get(aggregateId);

        if (null == t) {
            misses++;
            return Option.empty();
        } else {
            hits++;
            return new Some<T>(t);
        }
    }


    public String toString() {
        final int cacheAccesses = getHits() + getMisses();
        final String msg;
        if (cacheAccesses > 0) {
            msg = String.format(Locale.getDefault(), "%d items. %d accesses, %f2%% hit rate", getSize(), cacheAccesses, (double) getHits() * 100 / cacheAccesses);
        } else {
            msg = String.format(Locale.getDefault(), "%d items.  No accesses", getSize());
        }
        return msg;
    }
}
