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
package name.neuhalfen.todosimple.android.infrastructure.db.dbviews.todo;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import name.neuhalfen.todosimple.android.infrastructure.db.TodoSQLiteHelper;

public class LabelsForTaskTableImpl implements LabelsForTaskTable {


    private static final String DATABASE_CREATE_TODOS_LABELS = "create table "
            + TABLE_NAME + "("
            + COLUMN_LABEL_ID + " text not null, "
            + COLUMN_TASK_AGGREGATE_ID + " text not null, "
            + " PRIMARY KEY (" + COLUMN_LABEL_ID + "," + COLUMN_TASK_AGGREGATE_ID + ")"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_TODOS_LABELS);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TodoSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data"
        );
        db.execSQL("DROP TABLE IF EXISTS " +  TABLE_NAME );
        onCreate(db);
    }


}
