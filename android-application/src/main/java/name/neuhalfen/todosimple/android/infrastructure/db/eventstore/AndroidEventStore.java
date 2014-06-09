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
package name.neuhalfen.todosimple.android.infrastructure.db.eventstore;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import name.neuhalfen.todosimple.android.infrastructure.db.SQLiteToTransactionAdapter;
import name.neuhalfen.todosimple.android.infrastructure.db.eventstore.json.EventJsonSerializer;
import name.neuhalfen.todosimple.domain.infrastructure.EventStore;
import name.neuhalfen.todosimple.domain.model.AggregateRoot;
import name.neuhalfen.todosimple.domain.model.Event;
import name.neuhalfen.todosimple.domain.model.UniqueId;
import name.neuhalfen.todosimple.helper.Preconditions;
import org.joda.time.format.DateTimeFormatter;
import scala.Option;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.collection.mutable.MutableList;

import java.io.IOException;
import java.sql.SQLException;

public class AndroidEventStore<T extends AggregateRoot<T, Event<T>>> implements EventStore<T> {

    private final SQLiteToTransactionAdapter txAdapter;
    private final EventJsonSerializer serializer;
    private final DateTimeFormatter timestampFormatter;
    private final EventStoreTable table;

    public AndroidEventStore(SQLiteToTransactionAdapter txAdapter, EventJsonSerializer serializer, DateTimeFormatter timestampFormatter, EventStoreTable table) {
        this.txAdapter = txAdapter;
        this.serializer = serializer;
        this.timestampFormatter = timestampFormatter;
        this.table = table;
    }


    @Override
    public Option<Seq<Event<T>>> loadEvents(UniqueId<T> aggregateId) throws IOException {
        Preconditions.checkNotNull(aggregateId, "aggregateId must not be null");

        SQLiteDatabase db = txAdapter.getDb();
        Cursor cursor = table.queryForAggregateOrderByVersion(db, aggregateId);
        if (cursor.isAfterLast()) {
            return Option.apply(null);
        } else {

            try {
                int columnIndexEvent = cursor.getColumnIndexOrThrow(EventStoreTableImpl.Table.COLUMN_EVENT);
                scala.collection.mutable.MutableList<Event<T>> events = new MutableList<Event<T>>();
                //events.appendElem();
                while (cursor.moveToNext()) {
                    String eventJson = cursor.getString(columnIndexEvent);
                    Event<T> event = serializer.parseEvent(eventJson);
                    events.appendElem(event);
                }
                return Option.apply(events.toSeq());
            } catch (EventJsonSerializer.EventJsonSerializeException e) {
                throw new IOException(e);
            }
        }
    }

    @Override
    public void appendEvents(UniqueId<T> aggregateId, Seq<Event<T>> events) throws IOException {
        try {
            Iterator<Event<T>> eventIterator = events.iterator();
            SQLiteDatabase db = txAdapter.getDb();

            while (eventIterator.hasNext()) {
                Event event = eventIterator.next();
                Log.d("EventStore", String.format("%s += %s ", aggregateId.toString(), event.toString()));
                String eventJson = serializer.serializeEvent(event);
                table.record(db, aggregateId, event.newAggregateRootVersion(), timestampFormatter.print(event.occurredAt()), event.getClass().getName(), eventJson);
            }
        } catch (SQLException e) {
            throw new IOException(e);
        } catch (EventJsonSerializer.EventJsonSerializeException e) {
            throw new IOException(e);
        }
    }
}
