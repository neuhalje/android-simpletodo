package name.neuhalfen.todosimple.android.infrastructure.json
import name.neuhalfen.myscala.domain.model.Event
import name.neuhalfen.myscala.domain.model.TaskCreatedEvent
import name.neuhalfen.myscala.domain.model.TaskRenamedEvent
import pl.polidea.robospock.RoboSpecification

import static name.neuhalfen.todosimple.helper.TestConstants.uuid1
import static name.neuhalfen.todosimple.helper.TestConstants.uuid2

class EventJsonSerializerImplTest
        extends RoboSpecification {

    def "serializing and deserializing an TaskRenamedEvent returns an equal instance"() {
        given:
        EventJsonSerializer sut = new EventJsonSerializerImpl();

        TaskRenamedEvent event = new TaskRenamedEvent(uuid1, uuid2, 1, 2, "my new description");

        when:
        String serializedEvent = sut.serializeEvent(event);
        Event deserialized = sut.parseEvent(serializedEvent);

        then:
        event.equals(deserialized)
    }

    def "serializing and deserializing an TaskCreatedEvents returns an equal instance"() {
        given:
        EventJsonSerializer sut = new EventJsonSerializerImpl();
        TaskCreatedEvent event = new TaskCreatedEvent(uuid1, uuid2, 0, 1, "my description");

        when:
        String serializedEvent = sut.serializeEvent(event);
        Event deserialized = sut.parseEvent(serializedEvent);

        then:
        event.equals(deserialized)
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
