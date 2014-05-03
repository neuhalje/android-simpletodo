package name.neuhalfen.todosimple.android;

import android.os.Bundle;
import mortar.Blueprint;
import mortar.ViewPresenter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main implements Blueprint {
    @Override public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module(injects = { HelloActivity.class, MainView.class }) class Module {
    }

    @Singleton
    static class Presenter extends ViewPresenter<MainView> {
        private final SimpleDateFormat format = new SimpleDateFormat();

        private int serial = -1;

        @Inject
        Presenter() {
        }

        @Override protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            if (savedInstanceState != null && serial == -1) serial = savedInstanceState.getInt("serial");

            getView().show("Update #" + ++serial + " at " + format.format(new Date()));
        }

        @Override protected void onSave(Bundle outState) {
            super.onSave(outState);
            outState.putInt("serial", serial);
        }
    }
}
