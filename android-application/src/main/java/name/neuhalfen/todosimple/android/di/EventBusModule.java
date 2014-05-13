package name.neuhalfen.todosimple.android.di;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

import javax.inject.Singleton;

@Module(library = true)
public class EventBusModule {
    @Singleton
    @Provides
    @ForApplication
    EventBus provideEventBus() {
        return new EventBus();
    }
}
