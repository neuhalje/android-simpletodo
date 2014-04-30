package name.neuhalfen.todosimple.android.infrastructure;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import name.neuhalfen.myscala.domain.infrastructure.EventStore;
import name.neuhalfen.myscala.domain.model.Event;
import name.neuhalfen.todosimple.android.di.ForApplication;
import name.neuhalfen.todosimple.android.infrastructure.db.EventStoreTableImpl;
import name.neuhalfen.todosimple.android.infrastructure.db.SQLiteToTransactionAdapter;
import scala.Option;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.collection.mutable.MutableList;

import javax.inject.Inject;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

public class AndroidEventStore implements EventStore {


    @Inject
    @ForApplication
    SQLiteToTransactionAdapter txAdapter;

    @Inject
    EventJsonSerializer serializer;


    @Override
    public Option<Seq<Event>> loadEvents(UUID aggregateId) throws IOException {
        // FIXME
        SQLiteDatabase db = txAdapter.getDb();
        Cursor cursor = EventStoreTableImpl.queryForAggregateOrderByVersion(db, aggregateId);
        if (cursor.isAfterLast()) {
            return Option.apply(null);
        } else {

            int columnIndexEvent = cursor.getColumnIndexOrThrow(EventStoreTableImpl.Table.COLUMN_EVENT);
            scala.collection.mutable.MutableList<Event> events = new MutableList<Event>();
            //events.appendElem();
            String eventJson = cursor.getString(columnIndexEvent);
            Event event = serializer.parseEvent(eventJson);
            events.appendElem(event);
            return Option.apply(events.toSeq());
        }
    }

    @Override
    public void appendEvents(UUID aggregateId, Seq<Event> events) throws IOException {
        // FIXME
        try {
            Iterator<Event> eventIterator = events.iterator();
            SQLiteDatabase db = txAdapter.getDb();

            while (eventIterator.hasNext()) {
                Event event = eventIterator.next();
                Log.d("EventStore", String.format("%s += %s ", aggregateId.toString(), event.toString()));
                String eventJson = serializer.serializeEvent(event);
                EventStoreTableImpl.record(db, aggregateId, event.newAggregateRootVersion(), eventJson);
            }
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }
}
