package name.neuhalfen.todosimple.android.infrastructure;

import name.neuhalfen.myscala.domain.model.Event;
import name.neuhalfen.myscala.domain.model.TaskCreatedEvent;
import name.neuhalfen.myscala.domain.model.TaskRenamedEvent;
import name.neuhalfen.todosimple.android.infrastructure.json.EventJsonSerializer;
import name.neuhalfen.todosimple.android.infrastructure.json.EventJsonSerializerImpl;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static name.neuhalfen.todosimple.TestConstants.uuid1;
import static name.neuhalfen.todosimple.TestConstants.uuid2;
import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class EventJsonSerializerImplTest {
    // in case of idea f*ing up: https://github.com/cantinac/GradleTDD/tree/master/unitTest


    @Test(expected = NullPointerException.class)
    public void serialize_null_throwsNPE() throws EventJsonSerializer.EventJsonSerializeException {
        EventJsonSerializer sut = new EventJsonSerializerImpl();
        sut.serializeEvent(null);
    }

    @Ignore("string comparison for json is not a good idea")
    @Test
    public void serialize_TaskCreatedEvent_returnsCorrectJson() throws EventJsonSerializer.EventJsonSerializeException {
        EventJsonSerializer sut = new EventJsonSerializerImpl();

        TaskCreatedEvent event = new TaskCreatedEvent(uuid1, uuid2, 0, 1, "my description");
        String serializedEvent = sut.serializeEvent(event);
        assertEquals("{\"aggregate\":\"22222222-2222-2222-2222-222222222222\",\"description\":\"my description\",\"newAggregateVersion\":1,\"type\":\"TaskCreatedEvent\",\"originalAggregateVersion\":0,\"eventid\":\"11111111-1111-1111-1111-111111111111\"}", serializedEvent);
    }

    @Ignore("string comparison for json is not a good idea")
    @Test
    public void serialize_TaskRenamedEvent_returnsCorrectJson() throws EventJsonSerializer.EventJsonSerializeException {
        EventJsonSerializer sut = new EventJsonSerializerImpl();

        TaskRenamedEvent event = new TaskRenamedEvent(uuid1, uuid2, 1, 2, "my new description");
        String serializedEvent = sut.serializeEvent(event);
        assertEquals("{\"aggregate\":\"22222222-2222-2222-2222-222222222222\",\"newDescription\":\"my new description\",\"newAggregateVersion\":2,\"type\":\"TaskRenamedEvent\",\"originalAggregateVersion\":1,\"eventid\":\"11111111-1111-1111-1111-111111111111\"}", serializedEvent);
    }

    @Test
    public void serializeAndDeserialize_TaskRenamedEvent_equals() throws EventJsonSerializer.EventJsonSerializeException {
        EventJsonSerializer sut = new EventJsonSerializerImpl();

        TaskRenamedEvent event = new TaskRenamedEvent(uuid1, uuid2, 1, 2, "my new description");
        String serializedEvent = sut.serializeEvent(event);

        Event deserialized = sut.parseEvent(serializedEvent);

        assertEquals(event, deserialized);
    }

    @Test
    public void serializeAndDeserialize_TaskCreatedEvent_equals() throws EventJsonSerializer.EventJsonSerializeException {
        EventJsonSerializer sut = new EventJsonSerializerImpl();

        TaskCreatedEvent event = new TaskCreatedEvent(uuid1, uuid2, 0, 1, "my description");
        String serializedEvent = sut.serializeEvent(event);

        Event deserialized = sut.parseEvent(serializedEvent);

        assertEquals(event, deserialized);
    }

    @Test(expected = NullPointerException.class)
    public void parse_null_throwsNPE() throws EventJsonSerializer.EventJsonSerializeException {
        EventJsonSerializer sut = new EventJsonSerializerImpl();
        sut.parseEvent(null);
    }

}