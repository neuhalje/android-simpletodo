package name.neuhalfen.todosimple.android.mft;

import android.app.Activity;
import android.os.Bundle;
import mortar.Mortar;
import mortar.MortarActivityScope;
import mortar.MortarScope;
import name.neuhalfen.todosimple.android.R;
import name.neuhalfen.todosimple.android.TodoApplication;

public class HelloActivity extends Activity {
    private MortarActivityScope activityScope;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MortarScope parentScope = ((TodoApplication) getApplication()).getRootScope();
        activityScope = Mortar.requireActivityScope(parentScope, new Main());
        Mortar.inject(this, this);
        activityScope.onCreate(savedInstanceState);

        setContentView(R.layout.main_view);
    }

    @Override public Object getSystemService(String name) {
        if (Mortar.isScopeSystemService(name)) {
            return activityScope;
        }
        return super.getSystemService(name);
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        activityScope.onSaveInstanceState(outState);
    }

    @Override protected void onDestroy() {
        super.onDestroy();

        if (isFinishing() && activityScope != null) {
            activityScope.destroy();
            activityScope = null;
        }
    }
}
