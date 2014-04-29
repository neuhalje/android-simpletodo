package name.neuhalfen.todosimple.android.infrastructure.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import name.neuhalfen.myscala.domain.infrastructure.Transaction;
import name.neuhalfen.myscala.domain.infrastructure.TransactionRollbackException;
import name.neuhalfen.todosimple.android.di.ForApplication;

import javax.inject.Inject;

public class SQLiteToTransactionAdapter implements Transaction {

    @Inject
    @ForApplication
    TodoSQLiteHelper sqllite;

    private SQLiteDatabase db;

    public SQLiteDatabase getDb() {
        return db;
    }

    @Override
    public void beginTransaction() {
        Log.i("TX", "beginTransaction");
        db = sqllite.getWritableDatabase();
        db.beginTransaction();
    }

    @Override
    public void commit() throws TransactionRollbackException {
        Log.i("TX", "commit");
        db.setTransactionSuccessful();
        db.endTransaction();
        db = null;
    }

    @Override
    public void rollback() {
        Log.i("TX", "rollback");
        db.endTransaction();
        db = null;
    }
}
