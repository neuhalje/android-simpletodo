package name.neuhalfen.todosimple.android.view.base

import name.neuhalfen.todosimple.android.R
import name.neuhalfen.todosimple.android.view.task.TaskListScreen
import name.neuhalfen.todosimple.android.view.task.TaskListView
import name.neuhalfen.todosimple.helper.FlowHelper
import name.neuhalfen.todosimple.helper.MortarHelper
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import pl.polidea.robospock.RoboSpecification

@Config(manifest = "../android-application//src/main/AndroidManifest.xml")
class BaseActivityTest
        extends RoboSpecification {


    def "injection into BaseActivity works"() {
        setup:

        MortarHelper.setupDefaultMortar()

        when:
        // it is started
        def activity = Robolectric.buildActivity(BaseActivity.class).create().get();

        then:
        // All injected fields are injected
        activity.mortarContext != null
        activity.eventBus != null
        activity.taskCache != null
        activity.labelCache != null
        activity.mainFlow != null
    }

    def "injection into BaseActivity Presenter works"() {
        def main = new Main()
        setup:

        Main.Presenter
        MortarHelper.setupDefaultMortar()

        when:
        // it is started
        def activity = Robolectric.buildActivity(BaseActivity.class).create().get();

        then:
        // All injected fields are injected
        activity.eventBus != null
        activity.taskCache != null
        activity.labelCache != null
        activity.mainFlow != null
    }

    def "the loaderManager is available through getSystemService"() {
        setup:

        MortarHelper.setupDefaultMortar()
        def activity = Robolectric.buildActivity(BaseActivity.class).create().get();

        when:
        def loaderManager = activity.getSystemService(BaseActivity.NAME_NEUHALFEN_LOADER_MANAGER)

        then:
        loaderManager != null
    }


    def "The default screen is a task list screen"() {
        setup:

        MortarHelper.setupDefaultMortar()
        def activity = Robolectric.buildActivity(BaseActivity.class).create().get();

        when:
        def currentScreen =  activity.mainFlow.getBackstack().current().getScreen()

        then:
        currentScreen!=null
        currentScreen instanceof TaskListScreen
    }

    def "The default view is a task list view"() {
        setup:

        MortarHelper.setupMortar(new Main())
        def activity = Robolectric.buildActivity(BaseActivity.class).create().get();

        when:

        def view = FlowHelper.currentFlowViewFor(activity)

        then:
        view!=null
        view instanceof TaskListView
    }


}


