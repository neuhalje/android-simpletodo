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
