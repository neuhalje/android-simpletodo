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
package name.neuhalfen.todosimple.android.view.task;

import android.os.Bundle;
import flow.Flow;
import flow.Layout;
import mortar.Blueprint;
import mortar.ViewPresenter;
import name.neuhalfen.todosimple.android.BuildConfig;
import name.neuhalfen.todosimple.android.R;
import name.neuhalfen.todosimple.android.di.ForApplication;
import name.neuhalfen.todosimple.android.view.base.ActionBarOwner;
import name.neuhalfen.todosimple.android.view.base.Main;
import name.neuhalfen.todosimple.domain.application.TaskManagingApplication;
import name.neuhalfen.todosimple.domain.model.*;
import rx.util.functions.Action0;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Locale;

@Layout(R.layout.task_list_view)
public class TaskListScreen implements Blueprint {

    @Override
    public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override
    public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module(injects = TaskListView.class, addsTo = Main.Module.class)
    static class Module {
    }

    @Singleton
    public static class Presenter extends ViewPresenter<TaskListView> {
        private final Flow flow;
        private final ActionBarOwner actionBar;
        private final TaskManagingApplication taskApp;
        private final Locale userLocale;

        @Inject
        Presenter(Flow flow, ActionBarOwner actionBar, @ForApplication TaskManagingApplication taskApp, @ForApplication Locale userLocale) {
            this.flow = flow;
            this.actionBar = actionBar;
            this.taskApp = taskApp;
            this.userLocale = userLocale;
        }

        @Override
        public void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            TaskListView view = getView();
            if (view == null) return;

            ActionBarOwner.Config actionBarConfig = actionBar.getConfig().withOwner(this);

            actionBarConfig = actionBarConfig.withSoleAction(new ActionBarOwner.MenuAction("New Task", new Action0() {
                @Override
                public void call() {
                    flow.goTo(DetailScreen.forNewTask());
                }
            }, R.drawable.ic_action_add));

            if (BuildConfig.DEBUG) {
                actionBarConfig =
                        actionBarConfig.addAction(new ActionBarOwner.MenuAction("Create 20 Demo Tasks", new Action0() {
                            @Override
                            public void call() {
                                for (int i = 1; i < 20; i++) {
                                    CreateTaskCommand createTaskCommand = Commands.createTask(String.format(userLocale, "Todo #%0,10d", i), "some random description");
                                    taskApp.executeCommand(createTaskCommand);

                                    for (int version=1; version<50;version++){
                                        RenameTaskCommand renameTaskCommand = new RenameTaskCommand(CommandId.generateId(), createTaskCommand.aggregateRootId(), version, String.format(userLocale, "Renamed todo #%0,10d for the %d time", i,version-1), "some random, long description for this task! ");
                                        taskApp.executeCommand(renameTaskCommand);
                                    }
                                }
                                TaskListView view = getView();
                                if (view == null) return;
                                view.reloadQuery();

                            }
                        }));
                actionBarConfig =
                        actionBarConfig.addAction(new ActionBarOwner.MenuAction("Create deleted Tasks", new Action0() {
                            // Put some load on the DB
                            @Override
                            public void call() {

                                for (int i = 1; i < 500; i++) {
                                    CreateTaskCommand createTaskCommand = Commands.createTask(String.format(userLocale, "Todo #%0,10d", i), "some random description");
                                    taskApp.executeCommand(createTaskCommand);
                                    RenameTaskCommand renameTaskCommand = new RenameTaskCommand(CommandId.generateId(), createTaskCommand.aggregateRootId(), 1, String.format(userLocale, "Renamed todo #%0,10d", i), "some random, long description for this task! ");
                                    taskApp.executeCommand(renameTaskCommand);
                                    RenameTaskCommand renameTaskCommand2 = new RenameTaskCommand(CommandId.generateId(), createTaskCommand.aggregateRootId(), 2, String.format(userLocale, "Renamed again todo #%0,10d", i), "some random, and considerable longer description for this task! ");
                                    taskApp.executeCommand(renameTaskCommand2);
                                    DeleteTaskCommand deleteTaskCommand = new DeleteTaskCommand(CommandId.generateId(), createTaskCommand.aggregateRootId(), 3);
                                    taskApp.executeCommand(deleteTaskCommand);
                                }
                                TaskListView view = getView();
                                if (view == null) return;
                                view.reloadQuery();
                            }
                        }));
            }


            actionBar.setConfig(actionBarConfig);
            view.showTasks();
        }


        public void onTaskSelected(TaskId taskID) {
            flow.goTo(DetailScreen.forExistingTask(taskID));
        }
    }
}

