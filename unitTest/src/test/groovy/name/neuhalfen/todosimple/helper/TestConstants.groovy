package name.neuhalfen.todosimple.helper

import name.neuhalfen.todosimple.domain.model.EventId
import name.neuhalfen.todosimple.domain.model.TaskId

class TestConstants {
    public final static TaskId taskId1 = TaskId.fromString("11111111-1111-1111-1111-111111111111")
    public final static EventId eventId2 = EventId.fromString("22222222-2222-2222-2222-222222222222");
    public final static UUID uuid1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
    public final static UUID uuid2 = UUID.fromString("22222222-2222-2222-2222-222222222222");
}
