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
package name.neuhalfen.todosimple.android.infrastructure.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import name.neuhalfen.todosimple.android.infrastructure.db.dbviews.todo.TodoContentProvider;
import name.neuhalfen.todosimple.android.infrastructure.db.dbviews.todo.TodoTableImpl;
import name.neuhalfen.todosimple.android.infrastructure.db.eventstore.EventStoreTableImpl;

public class TodoSQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "todotable.db";
    private static final int DATABASE_VERSION = TodoContentProvider.TodoTable.TABLE_VERSION + EventStoreTableImpl.Table.TABLE_VERSION;

    public TodoSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        TodoTableImpl.onCreate(db);
        EventStoreTableImpl.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        TodoTableImpl.onUpgrade(db, oldVersion, newVersion);
        EventStoreTableImpl.onUpgrade(db, oldVersion, newVersion);
    }
}
