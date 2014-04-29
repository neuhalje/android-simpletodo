package name.neuhalfen.todosimple.android.infrastructure;

import android.util.Log;
import name.neuhalfen.myscala.domain.infrastructure.EventStore;
import name.neuhalfen.myscala.domain.infrastructure.impl.MemoryEventStore;
import name.neuhalfen.myscala.domain.model.Event;
import name.neuhalfen.todosimple.android.di.ForApplication;
import name.neuhalfen.todosimple.android.infrastructure.db.SQLiteToTransactionAdapter;
import scala.Option;
import scala.collection.Iterator;
import scala.collection.Seq;

import javax.inject.Inject;
import java.util.UUID;

public class AndroidEventStore implements EventStore {

    private EventStore cache = new MemoryEventStore();

    @Inject
    @ForApplication
    SQLiteToTransactionAdapter txAdapter;


    @Override
    public Option<Seq<Event>> loadEvents(UUID aggregateId) {
        // FIXME

        return cache.loadEvents(aggregateId);
    }

    @Override
    public void appendEvents(UUID aggregateId, Seq<Event> events) {
        // FIXME
        Iterator<Event> eventIterator = events.iterator();
        while (eventIterator.hasNext()) {
            Log.i("EventStore", String.format("%s += %s ", aggregateId.toString(), eventIterator.next().toString()));

        }
        cache.appendEvents(aggregateId, events);
    }
}
