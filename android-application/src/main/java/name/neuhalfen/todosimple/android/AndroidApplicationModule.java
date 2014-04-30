package name.neuhalfen.todosimple.android;

import android.content.Context;
import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;
import name.neuhalfen.myscala.domain.application.TaskManagingApplication;
import name.neuhalfen.myscala.domain.infrastructure.EventPublisher;
import name.neuhalfen.myscala.domain.infrastructure.EventStore;
import name.neuhalfen.todosimple.android.di.ForApplication;
import name.neuhalfen.todosimple.android.infrastructure.AndroidEventPublisher;
import name.neuhalfen.todosimple.android.infrastructure.AndroidEventStore;
import name.neuhalfen.todosimple.android.infrastructure.json.EventJsonSerializer;
import name.neuhalfen.todosimple.android.infrastructure.json.EventJsonSerializerImpl;
import name.neuhalfen.todosimple.android.infrastructure.contentprovider.TodoContentProviderImpl;
import name.neuhalfen.todosimple.android.infrastructure.db.SQLiteToTransactionAdapter;
import name.neuhalfen.todosimple.android.infrastructure.db.TodoSQLiteHelper;

import javax.inject.Singleton;

@Module(library = true, complete = true, injects = {AndroidEventStore.class, TodoContentProviderImpl.class, AndroidEventPublisher.class, SQLiteToTransactionAdapter.class}, includes = AndroidApplicationModule.EventBusModule.class)
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

    @Module(library = true)
    static class EventBusModule {
        @Singleton
        @Provides
        @ForApplication
        EventBus provideEventBus() {
            return new EventBus();
        }
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

    //}

    /*
    @Provides @Singleton LocationManager provideLocationManager() {
        return (LocationManager) application.getSystemService(LOCATION_SERVICE);
    }
     */
}
