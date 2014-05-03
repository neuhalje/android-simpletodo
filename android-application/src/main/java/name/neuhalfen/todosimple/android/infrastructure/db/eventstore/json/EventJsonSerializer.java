package name.neuhalfen.todosimple.android.infrastructure.db.eventstore.json;

import name.neuhalfen.todosimple.domain.model.Event;
import org.json.JSONException;

public interface EventJsonSerializer<T extends Event> {
    public static class EventJsonSerializeException extends Exception{
        public EventJsonSerializeException(JSONException e) {
            super(e);
        }
    }

    public Event parseEvent(String eventJson) throws  EventJsonSerializeException;
    public String serializeEvent(Event event) throws  EventJsonSerializeException;
}
