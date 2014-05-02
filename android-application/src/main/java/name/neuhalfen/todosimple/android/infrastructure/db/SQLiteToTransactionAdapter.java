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

    private static enum TX_STATE {
        OPEN {
            @Override
            public boolean isTxInClosedState() {
                return false;
            }
        }, COMMITTED {
            @Override
            public boolean isTxInClosedState() {
                return true;
            }
        }, ABORTED {
            @Override
            public boolean isTxInClosedState() {
                return true;
            }
        };


        public abstract boolean isTxInClosedState();
    }

    private TX_STATE state = TX_STATE.COMMITTED;

    public SQLiteDatabase getDb() {
        return db;
    }

    @Override
    public void beginTransaction() {
        Log.i("TX", "beginTransaction");
        if (!state.isTxInClosedState()) {
            throw new IllegalStateException("beginTransaction: Transaction is in non-closed state:" + state);
        }
        db = sqllite.getWritableDatabase();
        db.beginTransaction();
        state = TX_STATE.OPEN;
    }


    @Override
    public void commit() throws TransactionRollbackException {
        Log.i("TX", "commit");
        if (state.isTxInClosedState()) {
            throw new IllegalStateException("commit: Transaction is in closed state:" + state);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        state = TX_STATE.COMMITTED;
        db = null;
    }

    @Override
    public void rollback() {
        Log.i("TX", "rollback");
        if (state == TX_STATE.ABORTED) {
            return;
        }

        if (state.isTxInClosedState()) {
            throw new IllegalStateException("commit: Transaction is in closed state:" + state);
        }
        db.endTransaction();
        state = TX_STATE.ABORTED;
        db = null;
    }
}
