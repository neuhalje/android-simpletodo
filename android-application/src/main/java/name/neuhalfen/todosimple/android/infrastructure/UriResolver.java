package name.neuhalfen.todosimple.android.infrastructure;

import android.net.Uri;
import name.neuhalfen.todosimple.domain.model.AggregateRoot;
import name.neuhalfen.todosimple.domain.model.Event;
import name.neuhalfen.todosimple.domain.model.UniqueId;

import javax.annotation.CheckForNull;

/**
 * Resolve aggregate IDs to Uris used in content providers.
 * @param <T>
 */
public interface UriResolver<T extends AggregateRoot<T, Event<T>>> {
    /**
     * @param aggregateRootId
     * @return  a valid URI OR null (e.g. when this is not supported)
     */
    public @CheckForNull Uri resolveId(UniqueId<T> aggregateRootId);
}
