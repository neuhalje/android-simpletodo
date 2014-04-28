package name.neuhalfen.todosimple.android.di;

import android.app.Activity;
import android.os.Bundle;
import dagger.ObjectGraph;
import name.neuhalfen.todosimple.android.TodoApplication;

import java.util.Arrays;
import java.util.List;

/**
 * Base activity for DI.
 */
public class DIActivity extends Activity {
    private ObjectGraph activityGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Perform injection so that when this call returns all dependencies will be available for use.
        TodoApplication application = (TodoApplication) getApplication();
        activityGraph = application.getApplicationGraph().plus(getModules().toArray());
        activityGraph.inject(this);
    }

    @Override
    protected void onDestroy() {
        // Eagerly clear the reference to the activity graph to allow it to be garbage collected as
        // soon as possible.
        activityGraph = null;

        super.onDestroy();
    }

    /**
     * A list of modules to use for the individual activity graph. Subclasses can override this
     * method to provide additional modules provided they call and include the modules returned by
     * calling {@code super.getModules()}.
     */
    protected List<Object> getModules() {
        return Arrays.<Object>asList();
    }

    /**
     * Inject the supplied {@code object} using the activity-specific graph.
     */
    public void inject(Object object) {
        activityGraph.inject(object);
    }
}
