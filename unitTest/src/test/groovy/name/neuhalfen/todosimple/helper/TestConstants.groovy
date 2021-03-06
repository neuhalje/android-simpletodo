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
package name.neuhalfen.todosimple.helper

import name.neuhalfen.todosimple.domain.model.*
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

class TestConstants {
    public final static TaskId taskId1 = TaskId.fromString("11111111-1111-1111-1111-111111111111");
    public final static EventId<Task> taskEventId2 = EventId.fromString("22222222-2222-1111-2222-222222222222");

    public final static LabelId labelId1 = LabelId.fromString("11111111-2222-2222-1111-111111111111");
    public final static EventId<Label> labelEventId1 = EventId.fromString("22222222-1111-2222-2222-222222222222");

    public final
    static DateTime TIME_NOW = DateTime.parse("20140517T165800Z", ISODateTimeFormat.basicDateTimeNoMillis());
    public final
    static DateTime TIME_BEFORE = DateTime.parse("19990517T165800Z", ISODateTimeFormat.basicDateTimeNoMillis());
    public final
    static DateTime TIME_AFTER = DateTime.parse("30140517T165800Z", ISODateTimeFormat.basicDateTimeNoMillis());
}
