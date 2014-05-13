package name.neuhalfen.todosimple.android.di;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dagger.Module;
import dagger.Provides;
import flow.Parcer;
import name.neuhalfen.todosimple.android.TodoApplication;
import name.neuhalfen.todosimple.android.infrastructure.AndroidEventPublisher;
import name.neuhalfen.todosimple.android.infrastructure.db.SQLiteToTransactionAdapter;
import name.neuhalfen.todosimple.android.infrastructure.db.TodoSQLiteHelper;
import name.neuhalfen.todosimple.android.infrastructure.db.dbviews.DatabaseViewManager;
import name.neuhalfen.todosimple.android.infrastructure.db.dbviews.todo.TodoContentProviderImpl;
import name.neuhalfen.todosimple.android.infrastructure.db.dbviews.todo.TodoTableDatabaseViewManager;
import name.neuhalfen.todosimple.android.infrastructure.db.eventstore.AndroidEventStore;
import name.neuhalfen.todosimple.android.infrastructure.db.eventstore.json.EventJsonSerializer;
import name.neuhalfen.todosimple.android.infrastructure.db.eventstore.json.EventJsonSerializerImpl;
import name.neuhalfen.todosimple.android.view.base.GsonParcer;
import name.neuhalfen.todosimple.domain.application.TaskManagingApplication;
import name.neuhalfen.todosimple.domain.infrastructure.EventPublisher;
import name.neuhalfen.todosimple.domain.infrastructure.EventStore;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Module(library = true, complete = true, injects = { AndroidEventStore.class, TodoContentProviderImpl.class, AndroidEventPublisher.class, SQLiteToTransactionAdapter.class}, includes = EventBusModule.class)
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
    TaskManagingApplication provideTaskManagementApplication(@ForApplication EventStore eventStore, @ForApplication EventPublisher eventPublisher, @ForApplication SQLiteToTransactionAdapter tx) {
        return new TaskManagingApplication(eventStore, eventPublisher, tx);
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
    EventStore provideEventStore(AndroidEventStore es) {
        return es;
    }

    @Singleton
    @Provides
    @ForApplication
    EventPublisher provideEventPublisher(AndroidEventPublisher p) {
        return p;
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
    EventJsonSerializer provideEventJsonSerializer() {
        return new EventJsonSerializerImpl();
    }

    @Provides
    @ForApplication
    Collection<DatabaseViewManager> provideDatabaseViewUpdaters() {
        List<DatabaseViewManager> views = new ArrayList<DatabaseViewManager>();
        views.add(new TodoTableDatabaseViewManager());
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


    /*
    @Provides @Singleton LocationManager provideLocationManager() {
        return (LocationManager) application.getSystemService(LOCATION_SERVICE);
    }
     */
}
