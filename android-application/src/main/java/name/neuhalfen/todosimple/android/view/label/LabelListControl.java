package name.neuhalfen.todosimple.android.view.label;

import android.util.Log;
import flow.Layout;
import mortar.Blueprint;
import mortar.ViewPresenter;
import name.neuhalfen.todosimple.android.R;
import name.neuhalfen.todosimple.android.view.base.Main;
import name.neuhalfen.todosimple.android.view.task.DetailScreen;

import javax.inject.Inject;
import javax.inject.Singleton;

@Layout(R.layout.label_list)
public class LabelListControl implements Blueprint {

    @Override
    public String getMortarScopeName() {
        return DetailScreen.class.getName();
        //return getClass().getName();
    }

    @Override
    public Object getDaggerModule() {
        return new Module();
    }


    @dagger.Module(addsTo = Main.Module.class, injects = {LabelListView.class
            , LabelListControl.class})
    class Module {
    }


    @Singleton
    static class Presenter extends ViewPresenter<LabelListView> {

        @Inject
        public Presenter() {
            Log.i("LLC", "Iam here");
        }


    }

}
