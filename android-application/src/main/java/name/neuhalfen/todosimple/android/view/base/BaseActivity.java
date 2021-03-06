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

/*
 *  Based on sourcecode from Square Inc.
 */
package name.neuhalfen.todosimple.android.view.base;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import flow.Flow;
import mortar.Mortar;
import mortar.MortarActivityScope;
import mortar.MortarScope;
import mortar.MortarScopeDevHelper;
import name.neuhalfen.todosimple.android.BuildConfig;
import name.neuhalfen.todosimple.android.R;
import name.neuhalfen.todosimple.android.di.ForApplication;
import name.neuhalfen.todosimple.android.infrastructure.cache.GlobalEntityCache;
import name.neuhalfen.todosimple.android.view.base.notification.ViewShowNotificationCommand;
import name.neuhalfen.todosimple.domain.model.*;

import javax.inject.Inject;
import java.util.List;

import static android.content.Intent.ACTION_MAIN;
import static android.content.Intent.CATEGORY_LAUNCHER;
import static android.view.MenuItem.SHOW_AS_ACTION_ALWAYS;
import static android.view.MenuItem.SHOW_AS_ACTION_NEVER;

public class BaseActivity extends Activity implements ActionBarOwner.View {
    private final static class CroutonStyles {
        /**
         * Default style for alerting the user.
         */
        public final Style ALERT;
        /**
         * Default style for confirming an action.
         */
        public final Style CONFIRM;
        /**
         * Default style for general information.
         */
        public final Style INFO;

        public CroutonStyles(Style alert, Style confirm, Style info) {
            ALERT = alert;
            CONFIRM = confirm;
            INFO = info;
        }
    }

    private CroutonStyles croutonStyles;

    /**
     * Use to get a LoaderManager by calling #getSystemService(NAME_NEUHALFEN_LOADER_MANAGER)
     */
    public static final String NAME_NEUHALFEN_LOADER_MANAGER = "name.neuhalfen.LoaderManager";
    private MortarActivityScope activityScope;
    private List<ActionBarOwner.MenuAction> actionBarMenuActions;

    @Inject
    ActionBarOwner actionBarOwner;

    @Inject
    @ForApplication
    EventBus eventBus;

    @Inject
    @ForApplication
    GlobalEntityCache<Task> taskCache;

    @Inject
    @ForApplication
    GlobalEntityCache<Label> labelCache;

    Flow mainFlow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isWrongInstance()) {
            finish();
            return;
        }

        MortarScope parentScope = Mortar.getScope(getApplication());
        activityScope = Mortar.requireActivityScope(parentScope, new Main());
        Mortar.inject(this, this);

        this.croutonStyles = buildCroutonColors(getResources(), getTheme());

        eventBus.register(this);

        activityScope.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity);
        MainView mainView = (MainView) findViewById(R.id.container);
        mainFlow = mainView.getFlow();

        actionBarOwner.takeView(this);
    }

    private CroutonStyles buildCroutonColors(final Resources resources, final Resources.Theme theme) {
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(R.attr.croutonBackgroundColor, typedValue, true);
        final int bgColorValue = typedValue.data;

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        theme.resolveAttribute(R.attr.croutonPadding, typedValue, true);
        final int padding = (int) typedValue.getDimension(metrics);

        Style alert = new Style.Builder()
                .setBackgroundColorValue(bgColorValue)
                .setTextColorValue(resources.getColor(R.color.red))
                .setPaddingInPixels(padding)
                .build();
        Style confirm = new Style.Builder()
                .setBackgroundColorValue(bgColorValue)
                .setTextColorValue(resources.getColor(R.color.yellow))
                .setPaddingInPixels(padding)
                .build();
        Style info = new Style.Builder()
                .setBackgroundColorValue(bgColorValue)
                .setTextColorValue(resources.getColor(R.color.green))
                .setPaddingInPixels(padding)
                .build();
        return new CroutonStyles(alert, confirm, info);
    }

    @Override
    public Object getSystemService(String name) {
        if (Mortar.isScopeSystemService(name)) {
            return activityScope;
        }
        if (NAME_NEUHALFEN_LOADER_MANAGER.equals(name)) {
            return getLoaderManager();
        }

        return super.getSystemService(name);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        activityScope.onSaveInstanceState(outState);
    }

    /**
     * Inform the view about back events.
     */
    @Override
    public void onBackPressed() {
        // Give the view a chance to handle going back. If it declines the honor, let super do its thing.
        if (!mainFlow.goBack()) super.onBackPressed();
    }

    /**
     * Inform the view about up events.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            return mainFlow.goUp();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Configure the action bar menu as required by {@link ActionBarOwner.View}.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (actionBarMenuActions != null) {
            for (final ActionBarOwner.MenuAction actionBarMenuAction : actionBarMenuActions) {
                final MenuItem menuItem = menu.add(actionBarMenuAction.title)
                        .setShowAsActionFlags(SHOW_AS_ACTION_ALWAYS)
                        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                actionBarMenuAction.action.call();
                                return true;
                            }
                        });

                if (actionBarMenuAction.hasIcon()) {
                    menuItem.setIcon(actionBarMenuAction.icon);
                }
            }
        }
        if (BuildConfig.DEBUG || true) {
            menu.add("Log Scope Hierarchy")
                    .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            final String msg = MortarScopeDevHelper.scopeHierarchyToString(activityScope);
                            Toast.makeText(BaseActivity.this, msg, Toast.LENGTH_LONG).show();
                            Log.d("DemoActivity", msg);
                            return true;
                        }
                    })
            .setShowAsAction(SHOW_AS_ACTION_NEVER);
            menu.add("Log Cache Statistics")
                    .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            final String msg;

                            msg = String.format("Tasks: %s\nLabels: %s", taskCache.toString(), labelCache.toString());
                            Toast.makeText(BaseActivity.this, msg, Toast.LENGTH_LONG).show();
                            Log.d("DemoActivity", msg);
                            return true;
                        }
                    })
            .setShowAsAction(SHOW_AS_ACTION_NEVER);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Crouton.clearCroutonsForActivity(this);

        actionBarOwner.dropView(this);
        eventBus.unregister(this);

        // activityScope may be null in case isWrongInstance() returned true in onCreate()
        if (isFinishing() && activityScope != null) {
            MortarScope parentScope = Mortar.getScope(getApplication());
            parentScope.destroyChild(activityScope);
            activityScope = null;
        }
    }

    @Override
    public Context getMortarContext() {
        return this;
    }

    @Override
    public void setShowHomeEnabled(boolean enabled) {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
    }

    @Override
    public void setUpButtonEnabled(boolean enabled) {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(enabled);
        actionBar.setHomeButtonEnabled(enabled);
    }

    @Override
    public void setMenu(List<ActionBarOwner.MenuAction> action) {
        if (action != actionBarMenuActions) {
            actionBarMenuActions = action;
            invalidateOptionsMenu();
        }
    }

    /**
     * Dev tools and the play store (and others?) launch with a different intent, and so
     * lead to a redundant instance of this activity being spawned. <a
     * href="http://stackoverflow.com/questions/17702202/find-out-whether-the-current-activity-will-be-task-root-eventually-after-pendin"
     * >Details</a>.
     */
    private boolean isWrongInstance() {
        if (!isTaskRoot()) {
            Intent intent = getIntent();
            boolean isMainAction = intent.getAction() != null && intent.getAction().equals(ACTION_MAIN);
            return intent.hasCategory(CATEGORY_LAUNCHER) && isMainAction;
        }
        return false;
    }


    /**
     * Event bus callback
     */
    public void onEventMainThread(ViewShowNotificationCommand cmd) {
        final Style croutonStyle;
        switch (cmd.style) {
            case ALERT:
                croutonStyle = croutonStyles.ALERT;
                break;
            case CONFIRM:
                croutonStyle = croutonStyles.CONFIRM;
                break;
            case INFO:
                croutonStyle = croutonStyles.INFO;
                break;
            default:
                // cannot happen, just make the java compiler happy
                throw new RuntimeException();
        }

        final Crouton crouton;
        if (cmd.isFromTextResource()) {
            crouton = Crouton.makeText(this, cmd.textRessourceId, croutonStyle);
        } else {
            crouton = Crouton.makeText(this, cmd.text, croutonStyle);
        }

        crouton.show();
    }

    /**
     * Event bus callback
     */
    public void onEventMainThread(TaskRenamedEvent event) {
        Crouton.makeText(this, String.format("Task '%s' renamed ", event.newTitle()), croutonStyles.INFO).show();
    }

    /**
     * Event bus callback
     */
    public void onEventMainThread(TaskCreatedEvent event) {
        Crouton.makeText(this, String.format("Task '%s' created ", event.title()), croutonStyles.INFO).show();
    }

    /**
     * Event bus callback
     */
    public void onEventMainThread(TaskDeletedEvent event) {
        Crouton.makeText(this, String.format("Task '%s' deleted", event.id()), croutonStyles.ALERT).show();
    }
}

