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
package name.neuhalfen.todosimple.android.infrastructure.db.dbviews;

import android.content.Context;
import name.neuhalfen.todosimple.android.infrastructure.db.SQLiteToTransactionAdapter;
import name.neuhalfen.todosimple.domain.model.Event;

import java.util.List;

public interface DatabaseViewManager {

    /**
     * Update the database with the passed domain events. The events are guaranteed
     * to belong to a single aggregate root and are ordered by the aggregate version (earlier events first).
     * <p/>
     * - Do not call commit on the db connection!
     * - Call context.getContentResolver().notifyChange(...) where appropriate
     *
     * @param context
     * @param db      DO NOT COMMIT!
     * @param events
     */
    public void updateDBViewTables(Context context, SQLiteToTransactionAdapter db, List<Event> events);
}
