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

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import name.neuhalfen.todosimple.android.infrastructure.db.TodoSQLiteHelper;
import name.neuhalfen.todosimple.domain.model.TaskId;
import name.neuhalfen.todosimple.helper.Preconditions;

import java.sql.SQLException;

public class EventStoreTableImpl {
    public interface Table {
        public static final String COLUMN_ID = "_id";
        /**
         * UUID
         */
        public static final String COLUMN_AGGREGATE_ID = "aggregate_id";
        /**
         * int
         */
        public static final String COLUMN_AGGREGATE_VERSION = "version";
        /**
         * String
         */
        public static final String COLUMN_EVENT = "event";

        public static String[] ALL_COLUMNS = {COLUMN_AGGREGATE_ID, COLUMN_AGGREGATE_VERSION, COLUMN_EVENT, COLUMN_ID};

        int TABLE_VERSION = 3;
    }

    public static final String TABLE_EVENT = "event";

    // Database creation sql statement
    private static final String DATABASE_CREATE_TABLE = "create table "
            + TABLE_EVENT + "(" + Table.COLUMN_ID
            + " integer primary key autoincrement, "
            + Table.COLUMN_AGGREGATE_ID + " text not null,"
            + Table.COLUMN_AGGREGATE_VERSION + " not null,"
            + Table.COLUMN_EVENT + " text not null"
            + ");";

    private static final String DATABASE_CREATE_INDEX = String.format("create unique index idx_agg_vers on %s(%s,%s); ", TABLE_EVENT, Table.COLUMN_AGGREGATE_ID, Table.COLUMN_AGGREGATE_VERSION);


    public static void record(SQLiteDatabase db, TaskId aggregate, int newAggregateVersion, String event) throws SQLException {
        Preconditions.checkNotNull(aggregate, "aggregate must not be null");
        Preconditions.checkNotNull(db, "db must not be null");
        Preconditions.checkNotNull(event, "event must not be null");

        ContentValues values = new ContentValues();
        values.put(Table.COLUMN_AGGREGATE_ID, aggregate.toString());
        values.put(Table.COLUMN_AGGREGATE_VERSION, newAggregateVersion);
        values.put(Table.COLUMN_EVENT, event);
        db.insertOrThrow(TABLE_EVENT, null, values);
    }

    public static Cursor queryForAggregateOrderByVersion(SQLiteDatabase db, TaskId aggregate) {
        Preconditions.checkNotNull(aggregate, "aggregate must not be null");
        Preconditions.checkNotNull(db, "db must not be null");

        Cursor cursor = db.query(TABLE_EVENT, Table.ALL_COLUMNS, Table.COLUMN_AGGREGATE_ID + " = ?", new String[]{aggregate.toString()}, null, null, Table.COLUMN_AGGREGATE_VERSION + " ASC");
        return cursor;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_TABLE);
        database.execSQL(DATABASE_CREATE_INDEX);

    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TodoSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data"
        );
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);
        onCreate(db);
    }


}
