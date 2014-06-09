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

import name.neuhalfen.todosimple.android.di.ForApplication;
import name.neuhalfen.todosimple.domain.model.*;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static name.neuhalfen.todosimple.helper.Preconditions.checkNotNull;


public class LabelEventJsonSerializerImpl implements EventJsonSerializer<Label> {

    @Inject
    public LabelEventJsonSerializerImpl() {
    }


    @Inject
    @ForApplication
    DateTimeFormatter timestampFormatter;

    @Override
    public <T extends Event<Label>> T parseEvent(String eventJson) throws EventJsonSerializeException {
        checkNotNull(eventJson, "eventJson must not be null");

        try {
            JSONObject src = new JSONObject(eventJson);
            String type = src.getString("type");
            BaseEventSerializer<T> parser = (BaseEventSerializer<T>) map.get(type);

            if (null == parser) {
                throw new IllegalArgumentException("Cannot parse event of type '" + type + "' in " + eventJson);
            }

            T event = parser.parseEvent(src);
            return event;

        } catch (JSONException e) {
            throw new EventJsonSerializeException(e);
        }
    }

    @Override
    public <T extends Event<Label>> String serializeEvent(T event) throws EventJsonSerializeException {
        checkNotNull(event, "event must not be null");
        final String type = event.getClass().getSimpleName();
        BaseEventSerializer<T> serializer = (BaseEventSerializer<T>) map.get(type);

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


    private abstract class BaseEventSerializer<T extends Event<Label>> {

        protected static final String ORIGINAL_AGGREGATE_VERSION = "originalAggregateVersion";
        protected static final String NEW_AGGREGATE_VERSION = "newAggregateVersion";
        protected static final String EVENTID = "eventid";
        protected static final String AGGREGATEID = "aggregate";
        protected static final String TYPE = "type";
        protected static final String OCCURRED_AT = "occurredAt";

        protected static final String TITLE = "title";
        protected static final String NEW_TITLE = "newTitle";

        public abstract T parseEvent(JSONObject eventJson) throws JSONException;

        public void serializeEvent(JSONObject dest, T event) throws JSONException {
            checkNotNull(dest, "dest must not be null");
            checkNotNull(event, "event must not be null");

            dest.put(TYPE, event.getClass().getSimpleName());
            dest.put(AGGREGATEID, event.aggregateRootId());
            dest.put(EVENTID, event.id());
            dest.put(NEW_AGGREGATE_VERSION, event.newAggregateRootVersion());
            dest.put(ORIGINAL_AGGREGATE_VERSION, event.originalAggregateRootVersion());
            dest.put(OCCURRED_AT, timestampFormatter.print(event.occurredAt()));
        }
    }

    private final class LabelCreatedEventSerializer extends BaseEventSerializer<LabelCreatedEvent> {

        @Override
        public LabelCreatedEvent parseEvent(JSONObject eventJson) throws JSONException {
            final String aggregateId = eventJson.getString(AGGREGATEID);
            final String eventId = eventJson.getString(EVENTID);
            final int newAggregateVersion = eventJson.getInt(NEW_AGGREGATE_VERSION);
            final int originalAggregateVersion = eventJson.getInt(ORIGINAL_AGGREGATE_VERSION);

            final String title = eventJson.getString(TITLE);

            final String occurredAt = eventJson.getString(OCCURRED_AT);

            final EventId<Label> objectEventId = EventId.fromString(eventId);
            return new LabelCreatedEvent(objectEventId, LabelId.fromString(aggregateId), originalAggregateVersion, newAggregateVersion, timestampFormatter.parseDateTime(occurredAt), title);
        }

        public void serializeEvent(JSONObject dest, LabelCreatedEvent event) throws JSONException {
            super.serializeEvent(dest, event);
            dest.put(TITLE, event.title());
        }
    }

    private final class LabelRenamedEventSerializer extends BaseEventSerializer<LabelRenamedEvent> {

        public void serializeEvent(JSONObject dest, LabelRenamedEvent event) throws JSONException {
            super.serializeEvent(dest, event);
            dest.put(NEW_TITLE, event.newTitle());
        }

        @Override
        public LabelRenamedEvent parseEvent(JSONObject eventJson) throws JSONException {
            final String aggregateId = eventJson.getString(AGGREGATEID);
            final String eventId = eventJson.getString(EVENTID);
            final int newAggregateVersion = eventJson.getInt(NEW_AGGREGATE_VERSION);
            final int originalAggregateVersion = eventJson.getInt(ORIGINAL_AGGREGATE_VERSION);

            final String newTitle = eventJson.getString(NEW_TITLE);
            final String occurredAt = eventJson.getString(OCCURRED_AT);

            final EventId<Label> objectEventId = EventId.fromString(eventId);
            return new LabelRenamedEvent(objectEventId, LabelId.fromString(aggregateId), originalAggregateVersion, newAggregateVersion, timestampFormatter.parseDateTime(occurredAt), newTitle);
        }
    }

    private final class LabelDeletedEventSerializer extends BaseEventSerializer<LabelDeletedEvent> {

        public void serializeEvent(JSONObject dest, LabelDeletedEvent event) throws JSONException {
            super.serializeEvent(dest, event);
        }

        @Override
        public LabelDeletedEvent parseEvent(JSONObject eventJson) throws JSONException {
            final String aggregateId = eventJson.getString(AGGREGATEID);
            final String eventId = eventJson.getString(EVENTID);
            final int newAggregateVersion = eventJson.getInt(NEW_AGGREGATE_VERSION);
            final int originalAggregateVersion = eventJson.getInt(ORIGINAL_AGGREGATE_VERSION);
            final String occurredAt = eventJson.getString(OCCURRED_AT);


            final EventId<Label> objectEventId = EventId.fromString(eventId);
            return new LabelDeletedEvent(objectEventId, LabelId.fromString(aggregateId), originalAggregateVersion, newAggregateVersion, timestampFormatter.parseDateTime(occurredAt));
        }
    }

    private final static Map<String, BaseEventSerializer<?>> map = new HashMap<String, BaseEventSerializer<?>>();

    {
        map.put(LabelCreatedEvent.class.getSimpleName(), new LabelCreatedEventSerializer());
        map.put(LabelRenamedEvent.class.getSimpleName(), new LabelRenamedEventSerializer());
        map.put(LabelDeletedEvent.class.getSimpleName(), new LabelDeletedEventSerializer());
    }
}
