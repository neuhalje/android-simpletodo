package name.neuhalfen.todosimple.android.infrastructure.db.dbviews.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import name.neuhalfen.myscala.domain.model.Event;
import name.neuhalfen.myscala.domain.model.TaskCreatedEvent;
import name.neuhalfen.myscala.domain.model.TaskRenamedEvent;
import name.neuhalfen.todosimple.android.infrastructure.db.SQLiteToTransactionAdapter;
import name.neuhalfen.todosimple.android.infrastructure.db.TodoTableImpl;
import name.neuhalfen.todosimple.android.infrastructure.db.dbviews.DatabaseViewManager;

import java.util.List;

import static name.neuhalfen.todosimple.android.infrastructure.db.dbviews.todo.TodoContentProvider.TodoTable.*;

public class TodoTableDatabaseViewManager implements DatabaseViewManager {
    private final static String LOG_TAG = "TodoTableDatabaseView";

    @Override
    public void updateDBViewTables(Context context, SQLiteToTransactionAdapter txAdapter, List<Event> events) {
        final SQLiteDatabase db = txAdapter.getDb();


        for (Event event : events) {

            ContentValues values = new ContentValues();
            values.put(COLUMN_AGGREGATE_VERSION, event.newAggregateRootVersion());

            if (event instanceof TaskCreatedEvent) {
                final TaskCreatedEvent evt = (TaskCreatedEvent) event;

                // FIXME: add title to event/domain
                values.put(COLUMN_AGGREGATE_ID, evt.aggregateRootId().toString());
                values.put(COLUMN_TITLE, evt.description());
                values.put(COLUMN_DESCRIPTION, evt.description());

                long id = db.insert(TodoTableImpl.TABLE_TODOS, null, values);

                Uri uriWithId = TodoContentProvider.Factory.forContenProvider_Id(id);
                context.getContentResolver().notifyChange(uriWithId, null);

            } else if (event instanceof TaskRenamedEvent) {
                final TaskRenamedEvent evt = (TaskRenamedEvent) event;
                // FIXME: add title to event/domain
                values.put(COLUMN_TITLE, evt.newDescription());
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
                } else {
                    Log.e(LOG_TAG, "Unknown event:" + event.toString());
                }
            }
        }
    }
}
