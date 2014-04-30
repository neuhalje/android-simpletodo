package name.neuhalfen.todosimple.android.infrastructure;

import name.neuhalfen.myscala.domain.model.Event;

import static com.google.common.base.Preconditions.checkNotNull;

public class EventJsonSerializer {
    public Event parseEvent(String eventJson) {
        checkNotNull(eventJson,"json must not be null");
        return null;
    }

    public String serializeEvent(Event event) {
        checkNotNull(event,"event must not be null");
        return null;
    }
}
