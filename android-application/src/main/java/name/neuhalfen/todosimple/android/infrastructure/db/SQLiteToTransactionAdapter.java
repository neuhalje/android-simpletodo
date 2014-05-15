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

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import name.neuhalfen.todosimple.android.di.ForApplication;
import name.neuhalfen.todosimple.domain.infrastructure.Transaction;
import name.neuhalfen.todosimple.domain.infrastructure.TransactionRollbackException;
import name.neuhalfen.todosimple.helper.Preconditions;

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

        public boolean isTxInOpenState() {
            return !isTxInClosedState();
        }
    }

    private TX_STATE state = TX_STATE.COMMITTED;

    public SQLiteDatabase getDb() {
        Preconditions.checkState(state.isTxInOpenState(), "Transaction must be open but is in state %s.", state);
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
