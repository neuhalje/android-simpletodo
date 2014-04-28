package name.neuhalfen.todosimple.android.di;

import android.app.Fragment;
import android.os.Bundle;

public class DIFragment
        extends Fragment {
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((DIActivity) getActivity()).inject(this);
    }
}


