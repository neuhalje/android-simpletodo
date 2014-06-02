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

import android.content.Context;
import android.os.Bundle;
import mortar.Mortar;
import mortar.MortarScope;
import mortar.Presenter;
import rx.util.functions.Action0;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Allows shared configuration of the Android ActionBar.
 */
public class ActionBarOwner extends Presenter<ActionBarOwner.View> {
    public interface View {
        void setShowHomeEnabled(boolean enabled);

        void setUpButtonEnabled(boolean enabled);

        void setTitle(CharSequence title);

        void setMenu(List<MenuAction> action);

        Context getMortarContext();
    }

    public static class Config {
        public final boolean showHomeEnabled;
        public final boolean upButtonEnabled;
        public final CharSequence title;
        public final List<MenuAction> actions;

        public Config(boolean showHomeEnabled, boolean upButtonEnabled, CharSequence title,
                      MenuAction action) {
            this.showHomeEnabled = showHomeEnabled;
            this.upButtonEnabled = upButtonEnabled;
            this.title = title;


            List<MenuAction> firstItem = new ArrayList<MenuAction>();
            if (action!=null) {
                firstItem.add(action);
            }
            this.actions = Collections.unmodifiableList(firstItem);
        }

        private Config(boolean showHomeEnabled, boolean upButtonEnabled, CharSequence title,
                       List<MenuAction> actions) {
            this.showHomeEnabled = showHomeEnabled;
            this.upButtonEnabled = upButtonEnabled;
            this.title = title;
            this.actions = Collections.unmodifiableList(actions);
        }

        public Config withTitle(String title) {
            return new Config(showHomeEnabled, upButtonEnabled, title, actions);
        }

        public Config withAction(MenuAction action) {
            return new Config(showHomeEnabled, upButtonEnabled, title, action);
        }

        public Config addAction(MenuAction action) {
            List<MenuAction> newList = new ArrayList<MenuAction>(actions);
            newList.add(action);
            return new Config(showHomeEnabled, upButtonEnabled, title, newList);
        }
    }

    public static class MenuAction {
        public final CharSequence title;
        public final Action0 action;
        public final int icon;

        public MenuAction(CharSequence title, Action0 action, int icon) {
            this.title = title;
            this.action = action;
            this.icon = icon;
        }

        public MenuAction(CharSequence title, Action0 action) {
            this(title, action, Integer.MAX_VALUE);
        }

        public boolean hasIcon() {
            return icon != Integer.MAX_VALUE;
        }
    }

    private Config config;

    ActionBarOwner() {
    }

    @Override
    public void onLoad(Bundle savedInstanceState) {
        super.onLoad(savedInstanceState);
        if (config != null) update();
    }

    public void setConfig(Config config) {
        this.config = config;
        update();
    }

    public Config getConfig() {
        return config;
    }

    @Override
    protected MortarScope extractScope(View view) {
        return Mortar.getScope(view.getMortarContext());
    }

    private void update() {
        View view = getView();
        if (view == null) return;

        view.setShowHomeEnabled(config.showHomeEnabled);
        view.setUpButtonEnabled(config.upButtonEnabled);
        view.setTitle(config.title);
        view.setMenu(config.actions);
    }
}
