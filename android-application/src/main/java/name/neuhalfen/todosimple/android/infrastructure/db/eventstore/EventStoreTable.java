package name.neuhalfen.todosimple.android.infrastructure.db.eventstore;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import name.neuhalfen.todosimple.domain.model.UniqueId;
import name.neuhalfen.todosimple.helper.Preconditions;

import java.sql.SQLException;

public class EventStoreTable {

    public void record(SQLiteDatabase db, UniqueId<?> aggregateId, int newAggregateVersion, String occurredAt, String eventType, String event) throws SQLException {
        Preconditions.checkNotNull(aggregateId, "aggregateId must not be null");
        Preconditions.checkNotNull(db, "db must not be null");
        Preconditions.checkNotNull(event, "event must not be null");
        Preconditions.checkNotNull(occurredAt, "occurredAt must not be null");
        Preconditions.checkNotNull(eventType, "eventType must not be null");

        ContentValues values = new ContentValues();
        values.put(EventStoreTableImpl.Table.COLUMN_AGGREGATE_ID, aggregateId.toString());
        values.put(EventStoreTableImpl.Table.COLUMN_AGGREGATE_VERSION, newAggregateVersion);
        values.put(EventStoreTableImpl.Table.COLUMN_EVENT, event);
        values.put(EventStoreTableImpl.Table.COLUMN_EVENT_TYPE, eventType);
        values.put(EventStoreTableImpl.Table.COLUMN_OCCURRED_AT, occurredAt);
        db.insertOrThrow(EventStoreTableImpl.TABLE_EVENT, null, values);
    }

    public Cursor queryForAggregateOrderByVersion(SQLiteDatabase db, UniqueId<?> aggregateId) {
        Preconditions.checkNotNull(aggregateId, "aggregateId must not be null");
        Preconditions.checkNotNull(db, "db must not be null");

        Cursor cursor = db.query(EventStoreTableImpl.TABLE_EVENT, EventStoreTableImpl.Table.ALL_COLUMNS, EventStoreTableImpl.Table.COLUMN_AGGREGATE_ID + " = ?", new String[]{aggregateId.toString()}, null, null, EventStoreTableImpl.Table.COLUMN_AGGREGATE_VERSION + " ASC");
        return cursor;
    }

}
