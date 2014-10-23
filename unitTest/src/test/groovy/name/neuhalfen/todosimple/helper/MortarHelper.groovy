package name.neuhalfen.todosimple.helper

import dagger.ObjectGraph
import mortar.Blueprint
import mortar.Mortar
import mortar.MortarActivityScope
import mortar.MortarScope
import name.neuhalfen.todosimple.android.di.AndroidApplicationModule
import name.neuhalfen.todosimple.android.view.base.Main
import org.robolectric.Robolectric

class MortarHelper {

    private static void setupMortar(Blueprint blueprint) {
        MortarScope root = Mortar.createRootScope(false, ObjectGraph.create(new AndroidApplicationModule(Robolectric.application, Robolectric.application)));
        MortarActivityScope scope = Mortar.requireActivityScope(root, blueprint);
        scope.onCreate(null);
    }

    static void setupDefaultMortar() {
        setupMortar(new Main())
    }
}
