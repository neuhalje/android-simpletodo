package name.neuhalfen.todosimple.android;

import android.app.Application;
import dagger.ObjectGraph;
import edu.umd.cs.findbugs.annotations.NonNull;
import name.neuhalfen.todosimple.android.di.Injector;

import java.util.Arrays;
import java.util.List;

public class TodoApplication
        extends Application implements Injector {
    private ObjectGraph applicationGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        // inject(this)
    }

    protected List<Object> getModules() {
        return Arrays.asList(
                (Object) new AndroidApplicationModule(this)
        );
    }

    @NonNull
    public synchronized ObjectGraph getApplicationGraph() {
        if (null == applicationGraph) {
            // Cannot be done in onCreate bc/ the ContentProvider is initialized BEFORE Application::onCreate is called.
            // The CP must use the event bus, so here we are with an ugly "get and do something" solution
            applicationGraph = ObjectGraph.create(getModules().toArray());
        }
        return applicationGraph;
    }

    @Override
    public void inject(Object object) {
        getApplicationGraph().inject(object);
    }
}
