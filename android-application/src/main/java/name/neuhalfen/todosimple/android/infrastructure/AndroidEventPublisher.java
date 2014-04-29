package name.neuhalfen.todosimple.android.infrastructure;

import android.content.Context;
import android.widget.Toast;
import de.greenrobot.event.EventBus;
import name.neuhalfen.myscala.domain.infrastructure.EventPublisher;
import name.neuhalfen.myscala.domain.model.Event;
import name.neuhalfen.todosimple.android.di.ForApplication;

import javax.inject.Inject;
import java.util.List;

public class AndroidEventPublisher implements EventPublisher {

    @Inject
    @ForApplication
    Context context;

    @Inject
    @ForApplication
    EventBus eventBus;

    @Override
    public void publishEventsInTransaction(List<Event> events) {
        //
    }

    @Override
    public void publishEventsAfterCommit(List<Event> events) {
        for (Event event : events) {
            Toast.makeText(context, event.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
