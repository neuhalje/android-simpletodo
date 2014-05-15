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
package name.neuhalfen.todosimple.android.infrastructure.db.eventstore.json

import name.neuhalfen.todosimple.domain.model.Event
import name.neuhalfen.todosimple.domain.model.TaskCreatedEvent
import name.neuhalfen.todosimple.domain.model.TaskDeletedEvent
import name.neuhalfen.todosimple.domain.model.TaskRenamedEvent
import pl.polidea.robospock.RoboSpecification

import static name.neuhalfen.todosimple.helper.TestConstants.eventId2
import static name.neuhalfen.todosimple.helper.TestConstants.taskId1

class EventJsonSerializerImplTest
        extends RoboSpecification {

    def "serializing and deserializing a TaskRenamedEvent returns an equal instance"() {
        given:
        EventJsonSerializer sut = new EventJsonSerializerImpl();

        TaskRenamedEvent event = new TaskRenamedEvent(eventId2, taskId1, 1, 2, "my new description");

        when:
        String serializedEvent = sut.serializeEvent(event);
        Event deserialized = sut.parseEvent(serializedEvent);

        then:
        event.equals(deserialized)
    }

    def "serializing and deserializing a TaskCreatedEvents returns an equal instance"() {
        given:
        EventJsonSerializer sut = new EventJsonSerializerImpl();
        TaskCreatedEvent event = new TaskCreatedEvent(eventId2, taskId1, 0, 1, "my description");

        when:
        String serializedEvent = sut.serializeEvent(event);
        Event deserialized = sut.parseEvent(serializedEvent);

        then:
        event.equals(deserialized)
    }

    def "serializing and deserializing a TaskDeletedEvent returns an equal instance"() {
        given:
        EventJsonSerializer sut = new EventJsonSerializerImpl();

        Event event = new TaskDeletedEvent(eventId2, taskId1, 1, 2);

        when:
        String serializedEvent = sut.serializeEvent(event);
        Event deserialized = sut.parseEvent(serializedEvent);

        then:
        event.equals(deserialized)
    }


    def "serializing an unknown event fails"() {
        given:
        EventJsonSerializer sut = new EventJsonSerializerImpl();

        Event event = Mock(Event)

        when:
        String serializedEvent = sut.serializeEvent(event);

        then:
        thrown IllegalArgumentException
    }

    def "serializing null throws NPE"() {
        given:
        EventJsonSerializer sut = new EventJsonSerializerImpl();


        when:
        sut.serializeEvent(null);

        then:
        thrown NullPointerException
    }

    def "deserializing null throws NPE"() {
        given:
        EventJsonSerializer sut = new EventJsonSerializerImpl();


        when:
        sut.parseEvent(null);

        then:
        thrown NullPointerException
    }
}
