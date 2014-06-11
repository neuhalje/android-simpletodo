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

import name.neuhalfen.todosimple.android.di.Injector
import name.neuhalfen.todosimple.domain.model.*
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import pl.polidea.robospock.RoboSpecification

import static name.neuhalfen.todosimple.helper.TestConstants.*

@Config(manifest = "../android-application//src/main/AndroidManifest.xml")
class TaskEventJsonSerializerImplTest
        extends RoboSpecification {


    EventJsonSerializer<Task> sut;

    def setup() {
        def application = Robolectric.application
        sut = ((Injector) application).get(TaskEventJsonSerializerImpl.class);
    }

    def "serializing and deserializing a TaskRenamedEvent returns an equal instance"() {


        given:
        TaskRenamedEvent event = new TaskRenamedEvent(taskEventId2, taskId1, 1, 2, TIME_BEFORE, "my new title", "my new description");

        when:
        String serializedEvent = sut.serializeEvent(event);
        Event deserialized = sut.parseEvent(serializedEvent);

        then:
        event.equals(deserialized)
    }

    def "serializing and deserializing a TaskLabelRemovedEvent returns an equal instance"() {
        given:
        Event event = new TaskLabelRemovedEvent(taskEventId2, taskId1, 1, 2, TIME_BEFORE, LabelId.generateId());

        when:
        String serializedEvent = sut.serializeEvent(event);
        Event deserialized = sut.parseEvent(serializedEvent);

        then:
        event.equals(deserialized)
    }

    def "serializing and deserializing a TaskLabeledEvent returns an equal instance"() {
        given:
        Event event = new TaskLabeledEvent(taskEventId2, taskId1, 1, 2, TIME_BEFORE, LabelId.generateId());

        when:
        String serializedEvent = sut.serializeEvent(event);
        Event deserialized = sut.parseEvent(serializedEvent);

        then:
        event.equals(deserialized)
    }

    def "serializing and deserializing a TaskCreatedEvents returns an equal instance"() {
        given:
        TaskCreatedEvent event = new TaskCreatedEvent(taskEventId2, taskId1, 0, 1, TIME_BEFORE, "my new title", "my description");

        when:
        String serializedEvent = sut.serializeEvent(event);
        Event deserialized = sut.parseEvent(serializedEvent);

        then:
        event.equals(deserialized)
    }

    def "serializing and deserializing a TaskDeletedEvent returns an equal instance"() {
        given:
        Event event = new TaskDeletedEvent(taskEventId2, taskId1, 1, 2, TIME_NOW);

        when:
        String serializedEvent = sut.serializeEvent(event);
        Event deserialized = sut.parseEvent(serializedEvent);

        then:
        event.equals(deserialized)
    }


    def "serializing an unknown event fails"() {
        given:
        Event event = Mock(Event)

        when:
        String serializedEvent = sut.serializeEvent(event);

        then:
        thrown IllegalArgumentException
    }

    def "serializing null throws NPE"() {
        given:

        when:
        sut.serializeEvent(null);

        then:
        thrown NullPointerException
    }

    def "deserializing null throws NPE"() {
        given:

        when:
        sut.parseEvent(null);

        then:
        thrown NullPointerException
    }
}
