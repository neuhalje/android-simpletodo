package name.neuhalfen.todosimple.android.view.base

import android.content.Context
import dagger.ObjectGraph
import mortar.Blueprint
import mortar.Mortar
import mortar.MortarActivityScope
import mortar.MortarScope
import name.neuhalfen.todosimple.android.di.AndroidApplicationModule
import name.neuhalfen.todosimple.android.di.Injector
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import pl.polidea.robospock.RoboSpecification

@Config(manifest = "../android-application//src/main/AndroidManifest.xml")
class BaseActivityTest
        extends RoboSpecification {


    def "injection works"() {
        setup:
        //

        def context = Robolectric.application;


        MortarScope root = Mortar.createRootScope(false,  ObjectGraph.create(new AndroidApplicationModule(Robolectric.application, Robolectric.application )));
        MortarActivityScope scope =  Mortar.requireActivityScope(root, new Main());
        scope.onCreate(null);
        context.getSystemService(_) >> scope
        //   view.getContext() >> context;

        // given:

        def activity = Robolectric.buildActivity(BaseActivity.class).create().get();

        when:
        // it is started
        def s = ""

        then:
        // the TaskListView is shown
        activity.mortarContext != null
        activity.eventBus != null
        activity.taskCache != null
        activity.labelCache != null
        activity.mainFlow != null
    }

}


