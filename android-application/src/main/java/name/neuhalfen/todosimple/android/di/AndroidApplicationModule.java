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
package name.neuhalfen.todosimple.android.di;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;
import flow.Parcer;
import name.neuhalfen.todosimple.android.TodoApplication;
import name.neuhalfen.todosimple.android.infrastructure.AndroidEventPublisher;
import name.neuhalfen.todosimple.android.infrastructure.UriResolver;
import name.neuhalfen.todosimple.android.infrastructure.cache.GlobalEntityCache;
import name.neuhalfen.todosimple.android.infrastructure.db.SQLiteToTransactionAdapter;
import name.neuhalfen.todosimple.android.infrastructure.db.TodoSQLiteHelper;
import name.neuhalfen.todosimple.android.infrastructure.db.dbviews.DatabaseViewManager;
import name.neuhalfen.todosimple.android.infrastructure.db.dbviews.label.LabelContentProviderImpl;
import name.neuhalfen.todosimple.android.infrastructure.db.dbviews.label.LabelTableDatabaseViewManager;
import name.neuhalfen.todosimple.android.infrastructure.db.dbviews.label.LabelUriResolver;
import name.neuhalfen.todosimple.android.infrastructure.db.dbviews.todo.TodoContentProviderImpl;
import name.neuhalfen.todosimple.android.infrastructure.db.dbviews.todo.TodoTableDatabaseViewManager;
import name.neuhalfen.todosimple.android.infrastructure.db.dbviews.todo.TodoUriResolver;
import name.neuhalfen.todosimple.android.infrastructure.db.eventstore.AndroidEventStore;
import name.neuhalfen.todosimple.android.infrastructure.db.eventstore.EventStoreTable;
import name.neuhalfen.todosimple.android.infrastructure.db.eventstore.json.LabelEventJsonSerializerImpl;
import name.neuhalfen.todosimple.android.infrastructure.db.eventstore.json.TaskEventJsonSerializerImpl;
import name.neuhalfen.todosimple.android.view.base.GsonParcer;
import name.neuhalfen.todosimple.domain.application.LabelManagingApplication;
import name.neuhalfen.todosimple.domain.application.TaskManagingApplication;
import name.neuhalfen.todosimple.domain.infrastructure.EventPublisher;
import name.neuhalfen.todosimple.domain.infrastructure.EventStore;
import name.neuhalfen.todosimple.domain.model.Label;
import name.neuhalfen.todosimple.domain.model.Task;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

@Module(library = true, complete = true, injects = {AndroidEventStore.class, TodoContentProviderImpl.class, LabelContentProviderImpl.class, AndroidEventPublisher.class, SQLiteToTransactionAdapter.class, TaskEventJsonSerializerImpl.class, LabelEventJsonSerializerImpl.class}, includes = EventBusModule.class)
public class AndroidApplicationModule {
    private final TodoApplication application;

    public AndroidApplicationModule(TodoApplication application) {
        this.application = application;
    }

    /**
     * Allow the application context to be injected but require that it be annotated with
     * {@link ForApplication @Annotation} to explicitly differentiate it from an activity context.
     */
    @Singleton
    @Provides
    @ForApplication
    Context provideApplicationContext() {
        return application;
    }

    @Singleton
    @Provides
    @ForApplication
    TaskManagingApplication provideTaskManagementApplication(@ForApplication EventStore<Task> eventStore, @ForApplication EventPublisher<Task> eventPublisher, @ForApplication SQLiteToTransactionAdapter tx, @ForApplication GlobalEntityCache<Task> cache) {
        return new TaskManagingApplication(eventStore, eventPublisher, tx, cache);
    }

    @Singleton
    @Provides
    @ForApplication
    LabelManagingApplication provideLabelManagementApplication(@ForApplication EventStore<Label> eventStore, @ForApplication EventPublisher<Label> eventPublisher, @ForApplication SQLiteToTransactionAdapter tx, @ForApplication GlobalEntityCache<Label> cache) {
        return new LabelManagingApplication(eventStore, eventPublisher, tx, cache);
    }

    @Provides
    @Singleton
    @ForApplication
    GlobalEntityCache<Task> provideTaskCache() {
        return new GlobalEntityCache<Task>();
    }

    @Provides
    @Singleton
    @ForApplication
    GlobalEntityCache<Label> provideLabelCacheIF() {
        return new GlobalEntityCache<Label>();
    }

    @Singleton
    @Provides
    @ForApplication
    Locale provideUserLocale(@ForApplication Context context) {
        return context.getResources().getConfiguration().locale;
    }

    @Singleton
    @Provides
    @ForApplication
    TodoSQLiteHelper provideSQLiteHelper() {
        return new TodoSQLiteHelper(application);
    }

    //     /**
//     * All DIs needed in the domain model
//     */
//    @Module(injects = {TaskManagingApplication.class},includes = EventBusModule.class, library = true)
//    static class ForDomainModule {
//


    @Singleton
    @Provides
    @ForApplication
    EventStore<Label> provideLabelEventStoreCast(@ForApplication SQLiteToTransactionAdapter txAdapter, @ForApplication LabelEventJsonSerializerImpl serializer, @ForApplication DateTimeFormatter timestampFormatter, @ForApplication EventStoreTable table) {
        return new AndroidEventStore<Label>(txAdapter, serializer, timestampFormatter, table);
    }
    @Singleton
    @Provides
    @ForApplication
    EventStore<Task> provideTaskEventStoreCast(@ForApplication SQLiteToTransactionAdapter txAdapter, @ForApplication TaskEventJsonSerializerImpl serializer, @ForApplication DateTimeFormatter timestampFormatter, @ForApplication EventStoreTable table) {
        return new AndroidEventStore<Task>(txAdapter, serializer, timestampFormatter, table);
    }

    @Singleton
    @Provides
    @ForApplication
    EventPublisher<Task> provideTaskEventPublisher(@ForApplication SQLiteToTransactionAdapter txAdapter, @ForApplication EventBus eventBus, @ForApplication Context context, @ForApplication Collection<DatabaseViewManager<Task>> dbViews, @ForApplication UriResolver<Task> uriResolver) {
        final AndroidEventPublisher<Task> publisher = new AndroidEventPublisher<Task>(txAdapter, eventBus, context, dbViews, uriResolver);
        return publisher;
    }


    @Singleton
    @Provides
    @ForApplication
    EventPublisher<Label> provideLabelEventPublisher(@ForApplication SQLiteToTransactionAdapter txAdapter, @ForApplication EventBus eventBus, @ForApplication Context context, @ForApplication Collection<DatabaseViewManager<Label>> dbViews, @ForApplication UriResolver<Label> uriResolver) {
        final AndroidEventPublisher<Label> publisher = new AndroidEventPublisher<Label>(txAdapter, eventBus, context, dbViews, uriResolver);
        return publisher;
    }

    /**
     * --> Single Threaded!
     *
     * @return
     */
    @Singleton
    @Provides
    @ForApplication
    SQLiteToTransactionAdapter provideTransaction(SQLiteToTransactionAdapter t) {
        return t;
    }

    @Provides
    @Singleton
    @ForApplication
    TaskEventJsonSerializerImpl provideTaskEventJsonSerializer() {
        return application.get(TaskEventJsonSerializerImpl.class);
    }
    @Provides
    @Singleton
    @ForApplication
    LabelEventJsonSerializerImpl provideLabelEventJsonSerializer() {
        return application.get(LabelEventJsonSerializerImpl.class);
    }

    @Provides
    @ForApplication
    Collection<DatabaseViewManager<Task>> provideTaskDatabaseViewUpdaters() {
        List<DatabaseViewManager<Task>> views = new ArrayList<DatabaseViewManager<Task>>();
        views.add(new TodoTableDatabaseViewManager());
        return views;
    }

    @Provides
    @ForApplication
    Collection<DatabaseViewManager<Label>> provideLabelDatabaseViewUpdaters() {
        List<DatabaseViewManager<name.neuhalfen.todosimple.domain.model.Label>> views = new ArrayList<DatabaseViewManager<Label>>();
        views.add(new LabelTableDatabaseViewManager());
        return views;
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return new GsonBuilder().create();
    }

    @Provides
    @Singleton
    Parcer<Object> provideParcer(Gson gson) {
        return new GsonParcer<Object>(gson);
    }


    @Provides
    @Singleton
    @ForApplication
    DateTimeFormatter provideISOtimestampFormatter() {
        return ISODateTimeFormat.basicDateTime();
    }


    @Provides
    @Singleton
    @ForApplication
    UriResolver<Label> provideLabelUriResolver() {
        return new LabelUriResolver();
    }

    @Provides
    @Singleton
    @ForApplication
    UriResolver<Task> provideTaskUriResolver() {
        return new TodoUriResolver();
    }

    @Provides
    @Singleton
    @ForApplication
    EventStoreTable proveideEventStoreTable()
    {
        return new EventStoreTable();
    }

    /*
    @Provides @Singleton LocationManager provideLocationManager() {
        return (LocationManager) application.getSystemService(LOCATION_SERVICE);
    }
     */
}
