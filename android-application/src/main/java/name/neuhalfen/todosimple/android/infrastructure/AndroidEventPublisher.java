package name.neuhalfen.todosimple.android.infrastructure;

import android.content.Context;
import android.net.Uri;
import de.greenrobot.event.EventBus;
import name.neuhalfen.todosimple.android.di.ForApplication;
import name.neuhalfen.todosimple.android.infrastructure.db.SQLiteToTransactionAdapter;
import name.neuhalfen.todosimple.android.infrastructure.db.dbviews.DatabaseViewManager;
import name.neuhalfen.todosimple.android.infrastructure.db.dbviews.todo.TodoContentProvider;
import name.neuhalfen.todosimple.domain.infrastructure.EventPublisher;
import name.neuhalfen.todosimple.domain.model.Event;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AndroidEventPublisher implements EventPublisher {

    private final static String LOG_TAG = "AndroidEventPublisher";
    @Inject
    @ForApplication
    SQLiteToTransactionAdapter txAdapter;

    @Inject
    @ForApplication
    EventBus eventBus;

    @Inject
    @ForApplication
    Context context;

    @Inject
    @ForApplication
    Collection<DatabaseViewManager> dbViews;

    @Override
    public void publishEventsInTransaction(List<Event> events) {
        for (DatabaseViewManager dbView : dbViews) {
            dbView.updateDBViewTables(context, txAdapter, Collections.unmodifiableList(events));
        }
    }

    @Override
    public void publishEventsAfterCommit(List<Event> events) {
        for (Event event : events) {
            eventBus.post(event);
            Uri uriWithAggregateId = TodoContentProvider.Factory.forAggregateId(event.aggregateRootId());
            context.getContentResolver().notifyChange(uriWithAggregateId, null);
        }
    }
}
