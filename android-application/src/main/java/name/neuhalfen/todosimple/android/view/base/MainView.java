/*
Copyright 2014 Jens Neuhalfen

Licensed under the Apache License, Version 2.0 (the "License"); you may not use
this file except in compliance with the License. You may obtain a copy of the
License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed
under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
 */
package name.neuhalfen.todosimple.android.view.base;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import flow.Flow;
import mortar.Blueprint;
import mortar.Mortar;
import name.neuhalfen.todosimple.android.view.base.util.CanShowScreen;
import name.neuhalfen.todosimple.android.view.base.util.ScreenConductor;

import javax.inject.Inject;

public class MainView extends FrameLayout implements CanShowScreen<Blueprint> {

    @Inject
    Main.Presenter presenter;
    private final ScreenConductor<Blueprint> screenMaestro;

    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        screenMaestro = new ScreenConductor<Blueprint>(context, this);
        if (isInEditMode()) {
            return;
        }
        Mortar.inject(context, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (isInEditMode()) {
            return;
        }
        presenter.takeView(this);
    }

    public Flow getFlow() {
        return presenter.getFlow();
    }

    @Override
    public void showScreen(Blueprint screen, Flow.Direction direction) {
        screenMaestro.showScreen(screen, direction);
    }
}
