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

        applicationGraph = ObjectGraph.create(getModules().toArray());
    }

    protected List<Object> getModules() {
        return Arrays.asList(
                (Object) new AndroidApplicationModule(this)
        );
    }

    @NonNull
    public ObjectGraph getApplicationGraph() {
        if (null == applicationGraph) {
            throw new IllegalStateException("Application DI graph not yet created!");
        }
        return applicationGraph;
    }

    @Override
    public void inject(Object object) {
        getApplicationGraph().inject(object);
    }
}
