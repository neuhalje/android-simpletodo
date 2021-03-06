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

import name.neuhalfen.todosimple.domain.model.Event;
import org.json.JSONException;

public interface EventJsonSerializer<ENTITY> {
    public static class EventJsonSerializeException extends Exception {
        public EventJsonSerializeException(JSONException e) {
            super(e);
        }
    }

    public <T extends Event<ENTITY>> T parseEvent(String eventJson) throws EventJsonSerializeException;

    public <T extends Event<ENTITY>> String serializeEvent(T event) throws EventJsonSerializeException;
}
