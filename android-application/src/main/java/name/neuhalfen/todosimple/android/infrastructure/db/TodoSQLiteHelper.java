package name.neuhalfen.todosimple.android.infrastructure.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import name.neuhalfen.todosimple.android.domain.queries.TodoContentProvider;

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
