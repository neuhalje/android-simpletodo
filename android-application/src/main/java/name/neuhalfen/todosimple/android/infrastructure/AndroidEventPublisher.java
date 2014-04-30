package name.neuhalfen.todosimple.android.infrastructure;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.greenrobot.event.EventBus;
import name.neuhalfen.myscala.domain.infrastructure.EventPublisher;
import name.neuhalfen.myscala.domain.model.Event;
import name.neuhalfen.myscala.domain.model.TaskCreatedEvent;
import name.neuhalfen.myscala.domain.model.TaskRenamedEvent;
import name.neuhalfen.todosimple.android.di.ForApplication;
import name.neuhalfen.todosimple.android.infrastructure.db.SQLiteToTransactionAdapter;
import name.neuhalfen.todosimple.android.infrastructure.db.TodoTableImpl;

import javax.inject.Inject;
import java.util.List;

import static name.neuhalfen.todosimple.android.domain.queries.TodoContentProvider.TodoTable.*;

public class AndroidEventPublisher implements EventPublisher {

    private final static String LOG_TAG = "CreateView";
    @Inject
    @ForApplication
    SQLiteToTransactionAdapter txAdapter;

    @Inject
    @ForApplication
    EventBus eventBus;

    @Override
    public void publishEventsInTransaction(List<Event> events) {
        /*
        TODO:  Update the view table

        ContentValues values = new ContentValues();
        values.put
                (TodoContentProvider.TodoTable.COLUMN_TITLE, titleText.getText().toString());
        values.put
                (TodoContentProvider.TodoTable.COLUMN_DESCRIPTION, descriptionText.getText().toString());

        if (isEditExistingTask()) {
            getActivity().getContentResolver().update(todoUri, values, null, null);
        } else {
            todoUri = getActivity().getContentResolver().insert(TodoContentProvider.CONTENT_URI, values);
            dataState = DATA_STATE.LOADED;
        }
        */

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

                // TODO: getContext().getContentResolver().notifyChange(newUri, null);
            } else if (event instanceof TaskRenamedEvent) {
                final TaskRenamedEvent evt = (TaskRenamedEvent) event;
                // FIXME: add title to event/domain
                values.put(COLUMN_TITLE, evt.newDescription());
                values.put(COLUMN_DESCRIPTION, evt.newDescription());
                String whereClause = COLUMN_AGGREGATE_ID + "='" + evt.aggregateRootId() + "' AND " + COLUMN_AGGREGATE_VERSION + " = " + evt.originalAggregateRootVersion();

                int rowsUpdated = db.update(TodoTableImpl.TABLE_TODOS,
                        values,
                        whereClause,
                        null);
                if (rowsUpdated != 1) {
                    Log.e(LOG_TAG, String.format("Could not update view %s: Where clause \"%s\" matched %d rows (should match exactly 1).", TodoTableImpl.TABLE_TODOS, whereClause, rowsUpdated));
                    txAdapter.rollback();
                    throw new RuntimeException("Could not update view, maybe a race condition with the version?");
                }
                // TODO: getContext().getContentResolver().notifyChange(newUri, null);
            } else {
                Log.e(LOG_TAG, "Unknown event:" + event.toString());
            }
        }
    }

    @Override
    public void publishEventsAfterCommit(List<Event> events) {
        for (Event event : events) {
            eventBus.post(event);
        }
    }
}
