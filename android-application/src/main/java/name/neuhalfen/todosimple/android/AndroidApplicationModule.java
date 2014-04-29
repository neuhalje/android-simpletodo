package name.neuhalfen.todosimple.android;

import android.content.Context;
import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;
import name.neuhalfen.myscala.domain.application.TaskManagingApplication;
import name.neuhalfen.todosimple.android.di.ForApplication;

import javax.inject.Singleton;

@Module(library = true)
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
    TaskManagingApplication provideTaskManagementApplication() {
        return new TaskManagingApplication();
    }

    @Singleton
    @Provides
    @ForApplication
    EventBus provideEventBus() {
        return  new EventBus();
    }


    /*
    @Provides @Singleton LocationManager provideLocationManager() {
        return (LocationManager) application.getSystemService(LOCATION_SERVICE);
    }
     */
}
