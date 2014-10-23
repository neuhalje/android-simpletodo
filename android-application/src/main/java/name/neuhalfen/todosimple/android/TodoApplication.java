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
package name.neuhalfen.todosimple.android;

import android.app.Application;
import dagger.ObjectGraph;
import edu.umd.cs.findbugs.annotations.NonNull;
import mortar.Mortar;
import mortar.MortarScope;
import name.neuhalfen.todosimple.android.di.AndroidApplicationModule;
import name.neuhalfen.todosimple.android.di.Injector;

import java.util.Arrays;
import java.util.List;

public class TodoApplication
        extends Application implements Injector {
    private ObjectGraph applicationGraph;
    private MortarScope rootScope;

    @Override
    public void onCreate() {
        super.onCreate();
        rootScope = Mortar.createRootScope(BuildConfig.DEBUG, getApplicationGraph());
    }

    public MortarScope getRootScope() {
        return rootScope;
    }

    protected List<Object> getModules() {
        return Arrays.asList(
                (Object) new AndroidApplicationModule(this,this)
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

    @Override
    public <T> T get(Class<? extends T> type) {
        return applicationGraph.get(type);
    }

    @Override
    public Object getSystemService(String name) {
        if (Mortar.isScopeSystemService(name)) {
            return rootScope;
        }
        return super.getSystemService(name);
    }
}
