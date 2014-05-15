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
import android.os.Parcelable;
import dagger.Provides;
import de.greenrobot.event.EventBus;
import flow.Flow;
import flow.HasParent;
import flow.Layout;
import flow.Parcer;
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
import scala.Option;

import javax.inject.Inject;
import javax.inject.Singleton;

import static name.neuhalfen.todosimple.helper.Preconditions.checkNotNull;

@Layout(R.layout.task_detail_view)
public class DetailScreen implements HasParent<TaskListScreen>, Blueprint {
    static enum State {EXISTING, NEW}

    static final class Cmd {
        public final State cmd;
        public final TaskId taskId;

        private Cmd(State cmd, TaskId taskId) {
            this.cmd = cmd;
            this.taskId = taskId;
        }
    }

    private final TaskId taskId;
    private final State state;

    /*
     * Create
     */
    private DetailScreen() {
        this.state = State.NEW;
        this.taskId = TaskId.generateId();
    }

    /*
    * edit
     */
    private DetailScreen(TaskId taskId) {
        checkNotNull(taskId, "taskId must not be null");
        this.state = State.EXISTING;
        this.taskId = taskId;
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
//        @Provides
//        Task provideTask(@ForApplication TaskManagingApplication taskApp) {
//            return taskApp.loadTask(taskId).get();
//        }

        @Provides
        Cmd provideCmd() {
            return new Cmd(state, taskId);
        }

    }


    @Singleton
    static class Presenter extends ViewPresenter<DetailView> {
        static class TaskDTO {
            public final int version;
            public final TaskId id;
            public final String title;
            public final String description;

            public final State state;

            TaskDTO(TaskId id, int version, String title, String description, State state) {
                this.version = version;
                this.id = id;
                this.title = title;
                this.description = description;
                this.state = state;
            }

            public TaskDTO withTitle(String newTitle) {
                return new TaskDTO(id, version, newTitle, description, state);
            }

            public TaskDTO withDescription(String newDescription) {
                return new TaskDTO(id, version, title, newDescription, state);
            }

            public TaskDTO asSaved() {
                return new TaskDTO(id, version, title, description, State.EXISTING);
            }

            public TaskDTO withVersion(int newVersion) {
                return new TaskDTO(id, newVersion, title, description, state);
            }
        }


        private final TaskManagingApplication taskApp;
        private final ActionBarOwner actionBar;
        private final Parcer<Object> parcer;
        private final EventBus eventBus;
        private final Cmd cmd;
        private final Flow flow;


        @Inject
        Presenter(@ForApplication TaskManagingApplication taskApp, @ForApplication EventBus eventBus, ActionBarOwner actionBar, Parcer<Object> parcer, Cmd cmd, Flow flow) {
            this.taskApp = taskApp;
            this.actionBar = actionBar;
            this.parcer = parcer;
            this.eventBus = eventBus;
            this.cmd = cmd;
            this.flow = flow;
        }

        TaskDTO loadOrCreateTaskDTO(Cmd cmd) {
            if (cmd.cmd == State.NEW) {
                return new TaskDTO(TaskId.generateId(), 0, "new task title", "new task description", State.NEW);
            } else {
                final Option<Task> taskOption = taskApp.loadTask(cmd.taskId);
                if (taskOption.isDefined()) {
                    final Task task = taskOption.get();
                    return new TaskDTO(task._aggregateId(), task.version(), "TODO any title", task._description(), State.EXISTING);
                } else {
                    throw new IllegalStateException(String.format("Task %s not found", cmd.taskId.toString()));
                }
            }
        }

        @Override
        public void onLoad(Bundle savedState) {
            super.onLoad(savedState);
            DetailView view = getView();

            if (view != null) {
                eventBus.register(this);

                ActionBarOwner.Config actionBarConfig = actionBar.getConfig();

                actionBarConfig =
                        actionBarConfig.withAction(new ActionBarOwner.MenuAction("Save", new Action0() {
                            @Override
                            public void call() {
                                saveTask();
                            }
                        }, R.drawable.ic_action_save)).addAction(new ActionBarOwner.MenuAction("Delete", new Action0() {
                            @Override
                            public void call() {
                                deleteTaskOrAbortCreate();
                            }
                        }, R.drawable.ic_action_trash));

                final TaskDTO taskDTO = loadOrCreateTaskDTO(cmd);

                actionBar.setConfig(actionBarConfig.withTitle(taskDTO.description));

                view.setOriginalState(taskDTO);

                if (savedState != null) {
                    final Parcelable parcelable = savedState.getParcelable("edit-state");
                    view.setEditState(null != parcelable ? (TaskDTO) parcer.unwrap(parcelable) : null);
                }
            }
        }

        private void deleteTaskOrAbortCreate() {
            DetailView view = getView();
            if (null == view) {
                return;
            }


            final TaskDTO originalState = view.getOriginalState();

            switch (originalState.state) {
                case EXISTING:
                    Command command = new DeleteTaskCommand(CommandId.generateId(), originalState.id, originalState.version);
                    executeCommand(command);
                    break;
                case NEW:
                    // Ignore -- just close the editor
                    break;
            }

            flow.goBack();
        }

        private void saveTask() {
            DetailView view = getView();
            if (null == view) {
                return;
            }


            final TaskDTO editState = view.getEditState();

            final Command command;
            switch (editState.state) {
                case EXISTING:
                    command = new RenameTaskCommand(CommandId.generateId(), editState.id, editState.version, editState.description);
                    break;
                case NEW:
                    command = new CreateTaskCommand(CommandId.generateId(), editState.id, editState.description, 0);
                    break;
                default:
                    throw new IllegalStateException(String.format("There should be no third state but it is '%s'", editState.state));
            }
            executeCommand(command);
        }

        private void executeCommand(Command command) {
            try {
                taskApp.executeCommand(command);
            } catch (Exception e) {
                eventBus.post(ViewShowNotificationCommand.makeText(e.getLocalizedMessage(), ViewShowNotificationCommand.Style.ALERT));
            }
        }

        @Override
        public void onSave(Bundle outState) {
            super.onSave(outState);
            eventBus.unregister(this);
            final TaskDTO editState = getView().getEditState();
            outState.putParcelable("edit-state", null != editState ? parcer.wrap(editState) : null);
        }

        /**
         * Event bus callback
         */
        public void onEventMainThread(TaskRenamedEvent event) {
            final DetailView view = getView();
            if (null == view) {
                return;
            }

            final TaskDTO originalState = view.getOriginalState();

            if (!isEventRelatesToMyTask(event, originalState)) {
                return;
            }
            final TaskDTO newState = originalState.withDescription(event.newDescription()).withVersion(event.newAggregateRootVersion());

            view.setOriginalState(newState);
        }

        private boolean isEventRelatesToMyTask(Event event, TaskDTO originalState) {
            return (event.aggregateRootId().equals(originalState.id)
                    && event.originalAggregateRootVersion() == originalState.version);
        }

        /**
         * Event bus callback
         */
        public void onEventMainThread(TaskCreatedEvent event) {
            final DetailView view = getView();
            if (null == view) {
                return;
            }

            final TaskDTO originalState = view.getOriginalState();

            // TODO: check if the race condition of multiple out of order events matters
            if (!isEventRelatesToMyTask(event, originalState)) {
                return;
            }
            final TaskDTO newState = originalState.asSaved().withVersion(event.newAggregateRootVersion());

            view.setOriginalState(newState);
        }

    }
}
