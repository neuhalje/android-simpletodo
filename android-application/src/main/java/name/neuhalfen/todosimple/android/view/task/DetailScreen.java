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

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import dagger.Provides;
import de.greenrobot.event.EventBus;
import flow.Flow;
import flow.HasParent;
import flow.Layout;
import mortar.Blueprint;
import mortar.ViewPresenter;
import name.neuhalfen.todosimple.android.R;
import name.neuhalfen.todosimple.android.di.ForApplication;
import name.neuhalfen.todosimple.android.view.base.ActionBarOwner;
import name.neuhalfen.todosimple.android.view.base.Main;
import name.neuhalfen.todosimple.android.view.base.notification.ViewShowNotificationCommand;
import name.neuhalfen.todosimple.domain.application.TaskManagingApplication;
import name.neuhalfen.todosimple.domain.model.*;
import rx.util.functions.Action0;

import javax.inject.Inject;
import javax.inject.Singleton;

import static name.neuhalfen.todosimple.helper.Preconditions.checkNotNull;

@Layout(R.layout.task_detail_view)
public class DetailScreen implements HasParent<TaskListScreen>, Blueprint {

    static final class Cmd {
        public final TaskDTO.State cmd;
        public final TaskId taskId;

        private Cmd(TaskDTO.State cmd, TaskId taskId) {
            this.cmd = cmd;
            this.taskId = taskId;
        }
    }

    private final Cmd cmd;


    /*
     * Create
     */
    private DetailScreen() {
        this.cmd = new Cmd(TaskDTO.State.NEW, TaskId.generateId());
    }

    /*
    * edit
     */
    private DetailScreen(TaskId taskId) {
        checkNotNull(taskId, "taskId must not be null");
        this.cmd = new Cmd(TaskDTO.State.EXISTING, taskId);
    }

    @Override
    public TaskListScreen getParent() {
        return new TaskListScreen();
    }

    @Override
    public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override
    public Object getDaggerModule() {
        return new Module();
    }

    public static Blueprint forExistingTask(TaskId taskId) {
        checkNotNull(taskId, "TaskId must not be null");
        return new DetailScreen(taskId);
    }

    public static Blueprint forNewTask() {
        return new DetailScreen();
    }

    @dagger.Module(addsTo = Main.Module.class, injects = {DetailView.class
            , DetailScreen.class})
    class Module {
        @Provides
        Cmd provideCmd() {
            return cmd;
        }

    }


    @Singleton
    static class Presenter extends ViewPresenter<DetailView> {


        private final TaskManagingApplication taskApp;
        private final TaskDTOAdapter taskDTOAdapter;
        private final ActionBarOwner actionBar;
        private final EventBus eventBus;
        private final Flow flow;
        private final Context context;

        private Cmd cmd;


        @Inject
        Presenter(@ForApplication TaskManagingApplication taskApp, TaskDTOAdapter taskDTOAdapter, @ForApplication EventBus eventBus, ActionBarOwner actionBar, Cmd initialCommand, Flow flow, @ForApplication Context context) {
            this.taskApp = taskApp;
            this.taskDTOAdapter = taskDTOAdapter;
            this.actionBar = actionBar;
            this.eventBus = eventBus;
            this.cmd = initialCommand;
            this.flow = flow;
            this.context = context;
        }

        @Override
        public void onLoad(Bundle savedState) {
            super.onLoad(savedState);
            DetailView view = getView();

            if (view != null) {
                eventBus.register(this);

                ActionBarOwner.Config actionBarConfig = actionBar.getConfig().withOwner(this);

                actionBarConfig =
                        actionBarConfig
                                .withSoleAction(new ActionBarOwner.MenuAction("Save", new Action0() {
                                    @Override
                                    public void call() {
                                        saveTask();
                                    }
                                }, R.drawable.ic_action_save))
                                .addAction(new ActionBarOwner.MenuAction("Delete", new Action0() {
                                    @Override
                                    public void call() {
                                        deleteTaskOrAbortCreate();
                                    }
                                }, R.drawable.ic_action_trash))
                                .addAction(new ActionBarOwner.MenuAction("New Task", new Action0() {
                                    @Override
                                    public void call() {
                                        saveTask();
                                        Presenter.this.takeCommand(new Cmd(TaskDTO.State.NEW, TaskId.generateId()));
                                    }
                                }, R.drawable.ic_action_add));

                actionBar.setConfig(actionBarConfig);

                takeCommand(this.cmd);
                view.setAvailableLabelProvider(   new ArrayAdapter<String>(context,
                        android.R.layout.simple_dropdown_item_1line, new String[] {
                        "Belgium", "France", "Italy", "Germany", "Spain"}
                ));
            }
        }

        private void takeCommand(Cmd cmd) {
            this.cmd = cmd;

            final TaskDTO taskDTO = taskDTOAdapter.loadOrCreateTaskDTO(cmd);

            final DetailView view = getView();
            view.setTaskStaus(cmd.cmd);
            view.setTaskId(taskDTO.id);
            view.setEditedDescription(taskDTO.description);
            view.setEditedTitle(taskDTO.title);
            view.setTaskVersion(taskDTO.version);

            actionBar.setConfig(actionBar.getConfig().withTitle(taskDTO.title));
        }

        private void deleteTaskOrAbortCreate() {
            DetailView view = getView();
            if (null == view) {
                return;
            }


            switch (view.getTaskStaus()) {
                case EXISTING:
                    Command command = new DeleteTaskCommand(CommandId.generateId(), cmd.taskId, view.getTaskVersion());
                    executeCommand(command);
                    break;
                case NEW:
                    // Ignore -- just close the editor
                    break;
                case DELETED:
                    // Ignore -- just close the editor
                    break;
            }

            view.setTaskStaus(TaskDTO.State.DELETED);
            flow.goBack();
        }

        public void onCloseView() {
            saveTask();
        }

        private void saveTask() {
            DetailView view = getView();
            if (null == view) {
                return;
            }


            final Command command;
            switch (view.getTaskStaus()) {
                case EXISTING:
                    command = new RenameTaskCommand(CommandId.generateId(), this.cmd.taskId, view.getTaskVersion(), view.getEditedTitle(), view.getEditedDescription());
                    break;
                case NEW:
                    command = new CreateTaskCommand(CommandId.generateId(), this.cmd.taskId, view.getEditedTitle(), view.getEditedDescription(), 0);
                    break;
                case DELETED:
                    // nothing to do.Probably a stray "save" from closing the view after saving
                    return;
                default:
                    throw new IllegalStateException(String.format("There should be no third state but it is '%s'", view.getTaskStaus()));
            }
            executeCommand(command);
        }

        private void executeCommand(final Command command) {
            new AsyncTask<Command, Void, Void>() {
                @Override
                protected Void doInBackground(final Command... params) {
                    final Command command = params[0];
                    try {
                        taskApp.executeCommand(command);
                    } catch (Exception e) {
                        eventBus.post(ViewShowNotificationCommand.makeText(e.getLocalizedMessage(), ViewShowNotificationCommand.Style.ALERT));
                    }
                    return null;
                }
            }.doInBackground(command);
        }

        @Override
        public void onSave(Bundle outState) {
            super.onSave(outState);
            eventBus.unregister(this);
        }

        /**
         * Event bus callback
         */
        public void onEventMainThread(TaskRenamedEvent event) {
            final DetailView view = getView();
            if (null == view) {
                return;
            }

            if (!isMyAggregate(event)) {
                return;
            }

            view.setEditedDescription(event.newDescription());
            view.setEditedTitle(event.newTitle());
            view.setTaskVersion(event.newAggregateRootVersion());

            final ActionBarOwner.Config config = actionBar.getConfig();
            if (config.isOwner(this)) {
                actionBar.setConfig(config.withTitle(event.newTitle()));
            }
        }

        /**
         * Event bus callback
         */
        public void onEventMainThread(TaskCreatedEvent event) {
            final DetailView view = getView();
            if (null == view) {
                return;
            }

            if (!isMyAggregate(event)) {
                return;
            }


            view.setEditedDescription(event.description());
            view.setEditedTitle(event.title());
            view.setTaskVersion(event.newAggregateRootVersion());
            view.setTaskStaus(TaskDTO.State.EXISTING);

            final ActionBarOwner.Config config = actionBar.getConfig();
            if (config.isOwner(this)) {
                actionBar.setConfig(config.withTitle(event.title()));
            }
        }

        private boolean isMyAggregate(Event event) {
            return cmd.taskId.equals(event.aggregateRootId());
        }

    }
}
