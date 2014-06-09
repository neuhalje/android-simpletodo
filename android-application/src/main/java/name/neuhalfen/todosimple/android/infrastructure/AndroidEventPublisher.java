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
import name.neuhalfen.todosimple.android.infrastructure.db.SQLiteToTransactionAdapter;
import name.neuhalfen.todosimple.android.infrastructure.db.dbviews.DatabaseViewManager;
import name.neuhalfen.todosimple.domain.infrastructure.EventPublisher;
import name.neuhalfen.todosimple.domain.model.AggregateRoot;
import name.neuhalfen.todosimple.domain.model.Event;
import scala.collection.Iterator;
import scala.collection.Seq;

import java.util.Collection;

public class AndroidEventPublisher<T extends AggregateRoot<T, Event<T>>> implements EventPublisher<T> {

    private final static String LOG_TAG = "AndroidEventPublisher";

    //@Inject
    //public AndroidEventPublisher(@ForApplication SQLiteToTransactionAdapter txAdapter,@ForApplication  EventBus eventBus,@ForApplication  Context context,@ForApplication  Collection<DatabaseViewManager> dbViews,@ForApplication  UriResolver<T> uriResolver){
    public AndroidEventPublisher(SQLiteToTransactionAdapter txAdapter, EventBus eventBus, Context context, Collection<DatabaseViewManager> dbViews, UriResolver<T> uriResolver) {
        this.txAdapter = txAdapter;
        this.eventBus = eventBus;
        this.context = context;
        this.dbViews = dbViews;
        this.uriResolver = uriResolver;
    }

    private final SQLiteToTransactionAdapter txAdapter;
    private final EventBus eventBus;
    private final Context context;
    private final Collection<DatabaseViewManager> dbViews;
    private final UriResolver<T> uriResolver;

    @Override
    public void publishEventsInTransaction(Seq<Event<T>> events) {
        for (DatabaseViewManager dbView : dbViews) {
            dbView.updateDBViewTables(context, txAdapter, scala.collection.JavaConversions.seqAsJavaList(events));
        }
    }

    @Override
    public void publishEventsAfterCommit(Seq<Event<T>> events) {

        final Iterator<Event<T>> eventIterator = events.iterator();
        while (eventIterator.hasNext()) {
            Event<T> event = eventIterator.next();
            eventBus.post(event);
            Uri uriWithAggregateId = uriResolver.resolveId(event.aggregateRootId());
            context.getContentResolver().notifyChange(uriWithAggregateId, null);
        }
    }

}
