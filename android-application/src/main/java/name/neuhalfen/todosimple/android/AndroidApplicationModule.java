package name.neuhalfen.todosimple.android;

import android.content.Context;
import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;
import name.neuhalfen.myscala.domain.application.EventStore;
import name.neuhalfen.myscala.domain.application.TaskManagingApplication;
import name.neuhalfen.myscala.domain.infrastructure.MemoryEventStore;
import name.neuhalfen.todosimple.android.di.ForApplication;
import name.neuhalfen.todosimple.android.infrastructure.contentprovider.TodoContentProviderImpl;

import javax.inject.Singleton;

@Module(library = true, injects = {TodoContentProviderImpl.class}, includes = AndroidApplicationModule.ForDomainModule.class)
public class AndroidApplicationModule {
    private final TodoApplication application;

    public AndroidApplicationModule(TodoApplication application) {
        this.application = application;
    }

    /**
     * Allow the application context to be injected but require that it be annotated with
     * {@link ForApplication @Annotation} to explicitly differentiate it from an activity context.
     */
    @Provides
    @Singleton
    @ForApplication
    Context provideApplicationContext() {
        return application;
    }

    @Singleton
    @Provides
    @ForApplication
    TaskManagingApplication provideTaskManagementApplication(EventStore eventStore) {
        return new TaskManagingApplication(eventStore );
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

    /**
     * All DIs needed in the domain model
     */
    @Module(injects = {TaskManagingApplication.class},includes = EventBusModule.class)
    static class ForDomainModule {

        @Singleton
        @Provides
        EventStore provideEventStore() {
            return new MemoryEventStore();
        }

    }

    /*
    @Provides @Singleton LocationManager provideLocationManager() {
        return (LocationManager) application.getSystemService(LOCATION_SERVICE);
    }
     */
}
