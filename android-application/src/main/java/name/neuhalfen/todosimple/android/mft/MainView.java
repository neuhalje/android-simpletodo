package name.neuhalfen.todosimple.android.mft;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import flow.Flow;
import mortar.Blueprint;
import mortar.Mortar;
import name.neuhalfen.todosimple.android.mft.util.CanShowScreen;
import name.neuhalfen.todosimple.android.mft.util.ScreenConductor;

import javax.inject.Inject;

public class MainView extends FrameLayout implements CanShowScreen<Blueprint> {
    @Inject
    Main.Presenter presenter;
    private final ScreenConductor<Blueprint> screenMaestro;

    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Mortar.inject(context, this);
        screenMaestro = new ScreenConductor<Blueprint>(context, this);
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        presenter.takeView(this);
    }

    public Flow getFlow() {
        return presenter.getFlow();
    }

    @Override public void showScreen(Blueprint screen, Flow.Direction direction) {
        screenMaestro.showScreen(screen, direction);
    }
}
