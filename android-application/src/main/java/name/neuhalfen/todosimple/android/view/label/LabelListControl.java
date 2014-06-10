package name.neuhalfen.todosimple.android.view.label;

import android.os.Bundle;
import mortar.Blueprint;
import mortar.ViewPresenter;
import name.neuhalfen.todosimple.android.view.base.Main;
import name.neuhalfen.todosimple.domain.model.LabelId;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.ref.WeakReference;
import java.util.*;

class LabelListControl implements Blueprint {

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


        private WeakReference<LabelListView.OnLabelClickedListener> onLabelClickedListener;
        private final Map<LabelId, LabelDTO> assignedLabels;

        @Inject
        public Presenter() {
            assignedLabels = new HashMap<LabelId, LabelDTO>();
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

        public void onLabelClicked(LabelDTO label) {
            if (hasOnLabelClickListener()) {
                onLabelClickedListener.get().onLabelClicked(getView(), label);
            }
        }

        public void setOnLabelClickedListener(LabelListView.OnLabelClickedListener listener) {
            if (null == listener) {
                this.onLabelClickedListener = null;
            } else {
                this.onLabelClickedListener = new WeakReference<LabelListView.OnLabelClickedListener>(listener);
            }
        }

        private boolean hasOnLabelClickListener() {
            return null != onLabelClickedListener && onLabelClickedListener.get() != null;
        }

        @Override
        public void dropView(LabelListView view) {
            super.dropView(view);
            setOnLabelClickedListener(null);
        }
    }

}
