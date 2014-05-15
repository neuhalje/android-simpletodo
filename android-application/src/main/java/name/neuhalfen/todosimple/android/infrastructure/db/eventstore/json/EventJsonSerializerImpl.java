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
package name.neuhalfen.todosimple.android.infrastructure.db.eventstore.json;

import name.neuhalfen.todosimple.domain.model.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static name.neuhalfen.todosimple.helper.Preconditions.checkNotNull;


public class EventJsonSerializerImpl implements EventJsonSerializer<Event> {

    @Override
    public Event parseEvent(String eventJson) throws EventJsonSerializeException {
        checkNotNull(eventJson, "eventJson must not be null");

        try {
            JSONObject src = new JSONObject(eventJson);
            String type = src.getString("type");
            BaseEventSerializer parser = map.get(type);

            if (null == parser) {
                throw new IllegalArgumentException("Cannot parse event of type '" + type + "' in " + eventJson);
            }

            Event event = parser.parseEvent(src);
            return event;

        } catch (JSONException e) {
            throw new EventJsonSerializeException(e);
        }
    }

    @Override
    public String serializeEvent(Event event) throws EventJsonSerializeException {
        checkNotNull(event, "event must not be null");
        final String type = event.getClass().getSimpleName();
        BaseEventSerializer serializer = map.get(type);

        if (null == serializer) {
            throw new IllegalArgumentException("Cannot serialize event of type '" + type + "' in " + event.toString());
        }
        JSONObject dest = new JSONObject();
        try {
            serializer.serializeEvent(dest, event);
            return dest.toString();
        } catch (JSONException e) {
            throw new EventJsonSerializeException(e);
        }
    }

    private static abstract class BaseEventSerializer<T extends Event> {

        protected static final String ORIGINAL_AGGREGATE_VERSION = "originalAggregateVersion";
        protected static final String NEW_AGGREGATE_VERSION = "newAggregateVersion";
        protected static final String EVENTID = "eventid";
        protected static final String AGGREGATEID = "aggregate";
        protected static final String TYPE = "type";

        public abstract T parseEvent(JSONObject eventJson) throws JSONException;

        public void serializeEvent(JSONObject dest, T event) throws JSONException {
            checkNotNull(dest, "dest must not be null");
            checkNotNull(event, "event must not be null");

            dest.put(TYPE, event.getClass().getSimpleName());
            dest.put(AGGREGATEID, event.aggregateRootId());
            dest.put(EVENTID, event.id());
            dest.put(NEW_AGGREGATE_VERSION, event.newAggregateRootVersion());
            dest.put(ORIGINAL_AGGREGATE_VERSION, event.originalAggregateRootVersion());
        }
    }

    private final static class TaskCreatedEventSerializer extends BaseEventSerializer<TaskCreatedEvent> {
        protected static final String DESCRIPTION = "description";

        @Override
        public TaskCreatedEvent parseEvent(JSONObject eventJson) throws JSONException {
            final String aggregateId = eventJson.getString(AGGREGATEID);
            final String eventId = eventJson.getString(EVENTID);
            final int newAggregateVersion = eventJson.getInt(NEW_AGGREGATE_VERSION);
            final int originalAggregateVersion = eventJson.getInt(ORIGINAL_AGGREGATE_VERSION);

            final String description = eventJson.getString(DESCRIPTION);

            return new TaskCreatedEvent(EventId.fromString(eventId), TaskId.fromString(aggregateId), originalAggregateVersion, newAggregateVersion, description);
        }

        public void serializeEvent(JSONObject dest, TaskCreatedEvent event) throws JSONException {
            super.serializeEvent(dest, event);
            dest.put(DESCRIPTION, event.description());
        }
    }

    private final static class TaskRenamedEventSerializer extends BaseEventSerializer<TaskRenamedEvent> {
        protected static final String NEW_DESCRIPTION = "newDescription";

        public void serializeEvent(JSONObject dest, TaskRenamedEvent event) throws JSONException {
            super.serializeEvent(dest, event);
            dest.put(NEW_DESCRIPTION, event.newDescription());
        }

        @Override
        public TaskRenamedEvent parseEvent(JSONObject eventJson) throws JSONException {
            final String aggregateId = eventJson.getString(AGGREGATEID);
            final String eventId = eventJson.getString(EVENTID);
            final int newAggregateVersion = eventJson.getInt(NEW_AGGREGATE_VERSION);
            final int originalAggregateVersion = eventJson.getInt(ORIGINAL_AGGREGATE_VERSION);

            final String newDescription = eventJson.getString(NEW_DESCRIPTION);

            return new TaskRenamedEvent(EventId.fromString(eventId), TaskId.fromString(aggregateId), originalAggregateVersion, newAggregateVersion, newDescription);
        }
    }

    private final static class TaskDeletedEventSerializer extends BaseEventSerializer<TaskDeletedEvent> {

        public void serializeEvent(JSONObject dest, TaskDeletedEvent event) throws JSONException {
            super.serializeEvent(dest, event);
        }

        @Override
        public TaskDeletedEvent parseEvent(JSONObject eventJson) throws JSONException {
            final String aggregateId = eventJson.getString(AGGREGATEID);
            final String eventId = eventJson.getString(EVENTID);
            final int newAggregateVersion = eventJson.getInt(NEW_AGGREGATE_VERSION);
            final int originalAggregateVersion = eventJson.getInt(ORIGINAL_AGGREGATE_VERSION);


            return new TaskDeletedEvent(EventId.fromString(eventId), TaskId.fromString(aggregateId), originalAggregateVersion, newAggregateVersion);
        }
    }

    private final static Map<String, BaseEventSerializer<?>> map = new HashMap<String, BaseEventSerializer<?>>();

    {
        map.put(TaskCreatedEvent.class.getSimpleName(), new TaskCreatedEventSerializer());
        map.put(TaskRenamedEvent.class.getSimpleName(), new TaskRenamedEventSerializer());
        map.put(TaskDeletedEvent.class.getSimpleName(), new TaskDeletedEventSerializer());
    }
}
