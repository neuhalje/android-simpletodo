package name.neuhalfen.todosimple.todosimple.infrastructure.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TodoTable {
    public static final String TABLE_TODOS = "comments";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TODO = "comment";


    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_TODOS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_TODO
            + " text not null);";


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
