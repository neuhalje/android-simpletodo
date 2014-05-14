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
