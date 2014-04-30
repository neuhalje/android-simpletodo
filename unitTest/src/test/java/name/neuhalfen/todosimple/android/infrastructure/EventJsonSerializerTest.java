package name.neuhalfen.todosimple.android.infrastructure;

import org.junit.Test;

import static org.junit.Assert.*;

public class EventJsonSerializerTest {

    @Test(expected = NullPointerException.class)
    public void serialize_null_throwsNPE() {
        EventJsonSerializer sut =new EventJsonSerializer();
        sut.serializeEvent(null);
    }

    @Test(expected = NullPointerException.class)
    public void parse_null_throwsNPE() {
        EventJsonSerializer sut =new EventJsonSerializer();
        sut.parseEvent(null);
    }

}