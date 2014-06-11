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

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import name.neuhalfen.todosimple.android.infrastructure.db.SQLiteToTransactionAdapter;
import name.neuhalfen.todosimple.android.infrastructure.db.dbviews.DatabaseViewManager;
import name.neuhalfen.todosimple.domain.model.*;

import static name.neuhalfen.todosimple.android.infrastructure.db.dbviews.todo.TodoContentProvider.TodoTable.*;

public class TodoTableDatabaseViewManager implements DatabaseViewManager<Task> {
    private final static String LOG_TAG = "TodoTableDatabaseView";

    @Override
    public void updateDBViewTables(Context context, SQLiteToTransactionAdapter txAdapter, Iterable<Event<Task>> events) {
        final SQLiteDatabase db = txAdapter.getDb();


        for (Event event : events) {

            ContentValues values = new ContentValues();
            values.put(COLUMN_AGGREGATE_VERSION, event.newAggregateRootVersion());

            if (event instanceof TaskCreatedEvent) {
                final TaskCreatedEvent evt = (TaskCreatedEvent) event;
                handleTaskCreatedEvent(context, db, values, evt);
            } else if (event instanceof TaskRenamedEvent) {
                final TaskRenamedEvent evt = (TaskRenamedEvent) event;
                handleTaskRenamedEvent(txAdapter, db, values, evt);
            } else if (event instanceof TaskDeletedEvent) {
                final TaskDeletedEvent evt = (TaskDeletedEvent) event;
                handleTaskDeletedEvent(txAdapter, db, evt);
            } else if (event instanceof TaskLabeledEvent) {
                final TaskLabeledEvent evt = (TaskLabeledEvent) event;
                handleTaskLabeledEvent(txAdapter, db, evt);
            } else if (event instanceof TaskLabelRemovedEvent) {
                final TaskLabelRemovedEvent evt = (TaskLabelRemovedEvent) event;
                handleTaskLabelRemovedEvent(txAdapter, db,  evt);
            } else {
                Log.e(LOG_TAG, "Unknown event:" + event.toString());
            }
        }
    }


    private void handleTaskDeletedEvent(SQLiteToTransactionAdapter txAdapter, SQLiteDatabase db, TaskDeletedEvent evt) {
        String whereClause = COLUMN_AGGREGATE_ID + "='" + evt.aggregateRootId().toString() + "' AND " + COLUMN_AGGREGATE_VERSION + " = " + evt.originalAggregateRootVersion();
        int rowsDeleted = db.delete(TodoTableImpl.TABLE_TODOS,
                whereClause,
                null);
        if (rowsDeleted != 1) {
            Log.e(LOG_TAG, String.format("Could not update (delete) view %s: Where clause \"%s\" matched %d rows (should match exactly 1).", TodoTableImpl.TABLE_TODOS, whereClause, rowsDeleted));
            txAdapter.rollback();
            throw new RuntimeException("Could not update (delete)  view, maybe a race condition with the version?");
        }
    }

    private void handleTaskRenamedEvent(SQLiteToTransactionAdapter txAdapter, SQLiteDatabase db, ContentValues values, TaskRenamedEvent evt) {
        values.put(COLUMN_TITLE, evt.newTitle());
        values.put(COLUMN_DESCRIPTION, evt.newDescription());

        // does not work: http://code.google.com/p/android/issues/detail?id=56062
        // String whereClause = COLUMN_AGGREGATE_ID + "='?' AND " + COLUMN_AGGREGATE_VERSION + " = ?";
        // final String[] whereArgs = {evt.aggregateRootId().toString(), "" + evt.originalAggregateRootVersion()};

        String whereClause = COLUMN_AGGREGATE_ID + "='" + evt.aggregateRootId().toString() + "' AND " + COLUMN_AGGREGATE_VERSION + " = " + evt.originalAggregateRootVersion();
        int rowsUpdated = db.update(TodoTableImpl.TABLE_TODOS,
                values,
                whereClause,
                null);
        if (rowsUpdated != 1) {
            Log.e(LOG_TAG, String.format("Could not update view %s: Where clause \"%s\" matched %d rows (should match exactly 1).", TodoTableImpl.TABLE_TODOS, whereClause, rowsUpdated));
            txAdapter.rollback();
            throw new RuntimeException("Could not update view, maybe a race condition with the version?");
        }
    }

    private void updateAggregateVersionOrFail(SQLiteToTransactionAdapter txAdapter, SQLiteDatabase db,    Event<Task> evt) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_AGGREGATE_VERSION, evt.newAggregateRootVersion());

        // does not work: http://code.google.com/p/android/issues/detail?id=56062
        // String whereClause = COLUMN_AGGREGATE_ID + "='?' AND " + COLUMN_AGGREGATE_VERSION + " = ?";
        // final String[] whereArgs = {evt.aggregateRootId().toString(), "" + evt.originalAggregateRootVersion()};

        String whereClause = COLUMN_AGGREGATE_ID + "='" + evt.aggregateRootId().toString() + "' AND " + COLUMN_AGGREGATE_VERSION + " = " + evt.originalAggregateRootVersion();
        int rowsUpdated = db.update(TodoTableImpl.TABLE_TODOS,
                values,
                whereClause,
                null);
        if (rowsUpdated != 1) {
            Log.e(LOG_TAG, String.format("Could not update view %s: Where clause \"%s\" matched %d rows (should match exactly 1).", TodoTableImpl.TABLE_TODOS, whereClause, rowsUpdated));
            txAdapter.rollback();
            throw new RuntimeException("Could not update view, maybe a race condition with the version?");
        }
    }

    private void handleTaskCreatedEvent(Context context, SQLiteDatabase db, ContentValues values, TaskCreatedEvent evt) {
        values.put(COLUMN_AGGREGATE_ID, evt.aggregateRootId().toString());
        values.put(COLUMN_TITLE, evt.title());
        values.put(COLUMN_DESCRIPTION, evt.description());

        long id = db.insert(TodoTableImpl.TABLE_TODOS, null, values);

        Uri uriWithId = TodoContentProvider.Factory.forContenProvider_Id(id);
        context.getContentResolver().notifyChange(uriWithId, null);
    }

    private void handleTaskLabeledEvent(SQLiteToTransactionAdapter txAdapter, SQLiteDatabase db,  TaskLabeledEvent evt) {
        ContentValues values = new ContentValues();
        updateAggregateVersionOrFail(txAdapter,db,evt);

        values.put(LabelsForTaskTable.COLUMN_TASK_AGGREGATE_ID, evt.aggregateRootId().toString());
        values.put(LabelsForTaskTable.COLUMN_LABEL_ID, evt.label().toString());

        long id = db.insert(LabelsForTaskTable.TABLE_NAME, null, values);
    }

    private void handleTaskLabelRemovedEvent(SQLiteToTransactionAdapter txAdapter, SQLiteDatabase db,  TaskLabelRemovedEvent evt) {
        ContentValues values = new ContentValues();
        updateAggregateVersionOrFail(txAdapter,db,evt);

        String whereClause = LabelsForTaskTable.COLUMN_TASK_AGGREGATE_ID + "='" + evt.aggregateRootId().toString() + "' AND " + LabelsForTaskTable.COLUMN_LABEL_ID + "='" + evt.label().toString();
        int rowsDeleted = db.delete(LabelsForTaskTable.TABLE_NAME,
                whereClause,
                null);
        if (rowsDeleted != 1) {
            final String msg = String.format("Could not update (delete) view %s: Where clause \"%s\" matched %d rows (should match exactly 1).", LabelsForTaskTable.TABLE_NAME, whereClause, rowsDeleted);
            Log.e(LOG_TAG, msg);
            txAdapter.rollback();
            throw new RuntimeException(msg);
        }
    }

}
