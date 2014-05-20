package name.neuhalfen.todosimple.android.view.label;

import android.os.Bundle;
import android.util.Log;
import mortar.Blueprint;
import mortar.ViewPresenter;
import name.neuhalfen.todosimple.android.view.base.Main;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

public class LabelListControl implements Blueprint {

    @Override
    public String getMortarScopeName() {
        //return DetailScreen.class.getName();
        return getClass().getName();
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


        private final Map<UUID, LabelDTO> assignedLabels;

        @Inject
        public Presenter() {
            Log.i("LLC", "Iam here");
            assignedLabels = new HashMap<UUID, LabelDTO>();
        }

        @Override
        public void onSave(Bundle outState) {
            super.onSave(outState);
        }

        @Override
        public void onLoad(Bundle outState) {
            super.onLoad(outState);

            final LabelListView view = getView();
            if (view != null) {
                // FIXME: Implement Batch operation in view
                for (LabelDTO label : assignedLabels.values()) {
                    view.addLabelView(label);
                }
            }
        }

        public Set<LabelDTO> getAssignedLabels() {
            return Collections.unmodifiableSet(new HashSet<LabelDTO>(assignedLabels.values()));
        }

        public void assignLabel(LabelDTO label) {
            if (assignedLabels.containsKey(label.id)) {
                return;
            }

            assignedLabels.put(label.id, label);

            getView().addLabelView(label);
        }

        public void unassignLabel(LabelDTO label) {
            if (!assignedLabels.containsKey(label.id)) {
                return;
            }
            assignedLabels.remove(label.id);
            getView().removeLabelView(label);
        }
    }

}
