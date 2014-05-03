package name.neuhalfen.todosimple.android.mft;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import mortar.Mortar;
import name.neuhalfen.todosimple.android.R;

import javax.inject.Inject;

public class MainView extends LinearLayout {
    @Inject
    Main.Presenter presenter;

    private TextView textView;

    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Mortar.inject(context, this);
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        textView = (TextView) findViewById(R.id.text);
        presenter.takeView(this);
    }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
    }

    public void show(CharSequence stuff) {
        textView.setText(stuff);
    }
}
