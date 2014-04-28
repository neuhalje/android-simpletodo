package name.neuhalfen.todosimple.android;

import android.app.Application;
import dagger.ObjectGraph;

import java.util.Arrays;
import java.util.List;

public class TodoApplication
        extends Application {
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

    public ObjectGraph getApplicationGraph() {
        return applicationGraph;
    }
}
