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
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import name.neuhalfen.todosimple.android.view.base.notification.ViewShowNotificationCommand;
import name.neuhalfen.todosimple.domain.model.TaskCreatedEvent;
import name.neuhalfen.todosimple.domain.model.TaskDeletedEvent;
import name.neuhalfen.todosimple.domain.model.TaskRenamedEvent;

import javax.inject.Inject;
import java.util.List;

import static android.content.Intent.ACTION_MAIN;
import static android.content.Intent.CATEGORY_LAUNCHER;
import static android.view.MenuItem.SHOW_AS_ACTION_ALWAYS;

public class BaseActivity extends Activity implements ActionBarOwner.View {
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

    private Flow mainFlow;

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


        eventBus.register(this);

        activityScope.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity);
        MainView mainView = (MainView) findViewById(R.id.container);
        mainFlow = mainView.getFlow();

        actionBarOwner.takeView(this);
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
                menu.add(actionBarMenuAction.title)
                        .setShowAsActionFlags(SHOW_AS_ACTION_ALWAYS)
                        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                actionBarMenuAction.action.call();
                                return true;
                            }
                        });
            }
        }
        if (BuildConfig.DEBUG) {
            menu.add("Log Scope Hierarchy")
                    .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            Log.d("DemoActivity", MortarScopeDevHelper.scopeHierarchyToString(activityScope));
                            return true;
                        }
                    });
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
            activityScope.destroy();
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
                croutonStyle = Style.ALERT;
                break;
            case CONFIRM:
                croutonStyle = Style.CONFIRM;
                break;
            case INFO:
                croutonStyle = Style.INFO;
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
        Crouton.makeText(this, String.format("Task renamed to '%s", event.newDescription()), Style.INFO).show();
    }

    /**
     * Event bus callback
     */
    public void onEventMainThread(TaskCreatedEvent event) {
        Crouton.makeText(this, String.format("Task created '%s", event.description()), Style.INFO).show();
    }

    /**
     * Event bus callback
     */
    public void onEventMainThread(TaskDeletedEvent event) {
        Crouton.makeText(this, String.format("Task deleted '%s", event.id()), Style.INFO).show();
    }
}

