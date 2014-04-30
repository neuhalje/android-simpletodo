package name.neuhalfen.todosimple.android.infrastructure.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import name.neuhalfen.todosimple.android.domain.queries.TodoContentProvider;

public class TodoTableImpl implements TodoContentProvider.TodoTable {
    public static final String TABLE_TODOS = "todos";

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_TODOS + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_AGGREGATE_ID + " text not null unique,"
            + COLUMN_AGGREGATE_VERSION + " not null,"
            + COLUMN_TITLE + " text not null,"
            + COLUMN_DESCRIPTION + " text not null"
            +");";


    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);

    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TodoSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data"
        );
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODOS);
        onCreate(db);
    }


}
