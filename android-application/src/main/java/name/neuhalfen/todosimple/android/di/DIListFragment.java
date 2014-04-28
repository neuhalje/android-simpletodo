package name.neuhalfen.todosimple.android.di;

import android.app.ListFragment;
import android.os.Bundle;

public class DIListFragment
        extends ListFragment {
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((DIActivity) getActivity()).inject(this);
    }
}


