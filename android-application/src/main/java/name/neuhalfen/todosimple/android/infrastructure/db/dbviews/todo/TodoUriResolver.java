package name.neuhalfen.todosimple.android.infrastructure.db.dbviews.todo;

import android.net.Uri;
import name.neuhalfen.todosimple.android.infrastructure.UriResolver;
import name.neuhalfen.todosimple.domain.model.Task;
import name.neuhalfen.todosimple.domain.model.UniqueId;

public class TodoUriResolver implements UriResolver<Task> {
    @Override
    public Uri resolveId(UniqueId<Task> aggregateRootId) {
        return TodoContentProvider.Factory.forAggregateId(aggregateRootId);
    }
}
