package name.neuhalfen.todosimple.android.infrastructure.db.dbviews.label;

import android.net.Uri;
import name.neuhalfen.todosimple.android.infrastructure.UriResolver;
import name.neuhalfen.todosimple.domain.model.Label;
import name.neuhalfen.todosimple.domain.model.UniqueId;

public class LabelUriResolver implements UriResolver<Label> {
    @Override
    public Uri resolveId(UniqueId<Label> aggregateRootId) {
        return LabelContentProvider.Factory.forAggregateId(aggregateRootId);
    }
}
