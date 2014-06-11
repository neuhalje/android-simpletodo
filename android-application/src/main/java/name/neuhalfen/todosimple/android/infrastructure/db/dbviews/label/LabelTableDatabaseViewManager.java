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
package name.neuhalfen.todosimple.android.infrastructure.db.dbviews.label;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import name.neuhalfen.todosimple.android.infrastructure.db.SQLiteToTransactionAdapter;
import name.neuhalfen.todosimple.android.infrastructure.db.dbviews.DatabaseViewManager;
import name.neuhalfen.todosimple.domain.model.*;

import static name.neuhalfen.todosimple.android.infrastructure.db.dbviews.todo.TodoContentProvider.TodoTable.*;

public class LabelTableDatabaseViewManager implements DatabaseViewManager<Label> {
    private final static String LOG_TAG = "TodoTableDatabaseView";

    @Override
    public void updateDBViewTables(Context context, SQLiteToTransactionAdapter txAdapter, Iterable<Event<Label>> events) {
        final SQLiteDatabase db = txAdapter.getDb();


        for (Event event : events) {

            ContentValues values = new ContentValues();
            values.put(COLUMN_AGGREGATE_VERSION, event.newAggregateRootVersion());

            if (event instanceof LabelCreatedEvent) {
                final LabelCreatedEvent evt = (LabelCreatedEvent) event;
                handleLabelCreatedEvent(context, db, values, evt);
            } else if (event instanceof LabelRenamedEvent) {
                final LabelRenamedEvent evt = (LabelRenamedEvent) event;
                handleLabelRenamedEvent(txAdapter, db, values, evt);
            } else if (event instanceof LabelDeletedEvent) {
                final LabelDeletedEvent evt = (LabelDeletedEvent) event;
                handleLabelDeletedEvent(txAdapter, db, evt);
            } else {
                Log.e(LOG_TAG, "Unknown event:" + event.toString());
            }
        }
    }

    private void handleLabelDeletedEvent(SQLiteToTransactionAdapter txAdapter, SQLiteDatabase db, LabelDeletedEvent evt) {
        String whereClause = COLUMN_AGGREGATE_ID + "='" + evt.aggregateRootId().toString() + "' AND " + COLUMN_AGGREGATE_VERSION + " = " + evt.originalAggregateRootVersion();
        int rowsDeleted = db.delete(LabelTableImpl.TABLE_NAME,
                whereClause,
                null);
        if (rowsDeleted != 1) {
            Log.e(LOG_TAG, String.format("Could not update (delete) view %s: Where clause \"%s\" matched %d rows (should match exactly 1).", LabelTableImpl.TABLE_NAME, whereClause, rowsDeleted));
            txAdapter.rollback();
            throw new RuntimeException("Could not update (delete)  view, maybe a race condition with the version?");
        }
    }

    private void handleLabelRenamedEvent(SQLiteToTransactionAdapter txAdapter, SQLiteDatabase db, ContentValues values, LabelRenamedEvent evt) {
        values.put(COLUMN_TITLE, evt.newTitle());

        // does not work: http://code.google.com/p/android/issues/detail?id=56062
        // String whereClause = COLUMN_AGGREGATE_ID + "='?' AND " + COLUMN_AGGREGATE_VERSION + " = ?";
        // final String[] whereArgs = {evt.aggregateRootId().toString(), "" + evt.originalAggregateRootVersion()};

        String whereClause = COLUMN_AGGREGATE_ID + "='" + evt.aggregateRootId().toString() + "' AND " + COLUMN_AGGREGATE_VERSION + " = " + evt.originalAggregateRootVersion();
        int rowsUpdated = db.update(LabelTableImpl.TABLE_NAME,
                values,
                whereClause,
                null);
        if (rowsUpdated != 1) {
            Log.e(LOG_TAG, String.format("Could not update view %s: Where clause \"%s\" matched %d rows (should match exactly 1).", LabelTableImpl.TABLE_NAME, whereClause, rowsUpdated));
            txAdapter.rollback();
            throw new RuntimeException("Could not update view, maybe a race condition with the version?");
        }
    }

    private void handleLabelCreatedEvent(Context context, SQLiteDatabase db, ContentValues values, LabelCreatedEvent evt) {
        values.put(COLUMN_AGGREGATE_ID, evt.aggregateRootId().toString());
        values.put(COLUMN_TITLE, evt.title());

        long id = db.insert(LabelTableImpl.TABLE_NAME, null, values);

        Uri uriWithId = LabelContentProvider.Factory.forContentProvider_Id(id);
        context.getContentResolver().notifyChange(uriWithId, null);
    }

}
