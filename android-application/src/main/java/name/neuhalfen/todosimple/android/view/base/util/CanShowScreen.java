package name.neuhalfen.todosimple.android.view.base.util;

import flow.Flow;
import mortar.Blueprint;

public interface CanShowScreen<S extends Blueprint> {
    void showScreen(S screen, Flow.Direction direction);
}
