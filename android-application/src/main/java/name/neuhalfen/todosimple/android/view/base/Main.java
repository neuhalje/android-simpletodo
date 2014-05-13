/*
 * Copyright 2013 Square Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package name.neuhalfen.todosimple.android.view.base;

import dagger.Provides;
import flow.Flow;
import flow.HasParent;
import flow.Parcer;
import mortar.Blueprint;
import name.neuhalfen.todosimple.android.di.AndroidApplicationModule;
import name.neuhalfen.todosimple.android.view.base.util.FlowOwner;
import name.neuhalfen.todosimple.android.view.task.TaskListScreen;
import rx.util.functions.Action0;

import javax.inject.Inject;
import javax.inject.Singleton;

public class Main implements Blueprint {

    @Override
    public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override
    public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module( //
            includes = BaseActivityModule.class,
            injects = MainView.class,
            addsTo = AndroidApplicationModule.class, //
            library = true //
    )
    public static class Module {
        @Provides
        @MainScope
        Flow provideFlow(Presenter presenter) {
            return presenter.getFlow();
        }
    }

    @Singleton
    public static class Presenter extends FlowOwner<Blueprint, MainView> {
        private final ActionBarOwner actionBarOwner;

        @Inject
        Presenter(Parcer<Object> flowParcer, ActionBarOwner actionBarOwner) {
            super(flowParcer);
            this.actionBarOwner = actionBarOwner;
        }

        @Override
        public void showScreen(Blueprint newScreen, Flow.Direction direction) {
            boolean hasUp = newScreen instanceof HasParent;
            String title = newScreen.getClass().getSimpleName();
            ActionBarOwner.MenuAction menu =
                    hasUp ? null : new ActionBarOwner.MenuAction("Friends", new Action0() {
                        @Override
                        public void call() {
                            //onFriendsListPicked();
                        }
                    });
            actionBarOwner.setConfig(new ActionBarOwner.Config(false, hasUp, title, menu));

            super.showScreen(newScreen, direction);
        }

        @Override
        protected Blueprint getFirstScreen() {
            return new TaskListScreen();
        }

    }
}
