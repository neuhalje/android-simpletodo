package name.neuhalfen.todosimple.android.infrastructure;

import android.util.Log;
import de.greenrobot.event.EventBus;
import name.neuhalfen.myscala.domain.infrastructure.EventPublisher;
import name.neuhalfen.myscala.domain.model.Event;
import name.neuhalfen.todosimple.android.di.ForApplication;
import name.neuhalfen.todosimple.android.infrastructure.db.SQLiteToTransactionAdapter;

import javax.inject.Inject;
import java.util.List;

public class AndroidEventPublisher implements EventPublisher {

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

        for (Event event : events) {
            Log.i("CreateView", event.toString());
        }
    }

    @Override
    public void publishEventsAfterCommit(List<Event> events) {
        for (Event event : events) {
            eventBus.post(event);
        }
    }
}
