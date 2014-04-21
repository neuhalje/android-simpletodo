package name.neuhalfen.todosimple.todosimple.infrastructure.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TodoSQLiteHelper extends SQLiteOpenHelper {
    public TodoSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        TodoTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        TodoTable.onUpgrade(db, oldVersion, newVersion);

    }
}
