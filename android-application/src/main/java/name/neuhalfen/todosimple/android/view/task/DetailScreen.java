package name.neuhalfen.todosimple.android.view.task;

import android.os.Bundle;
import android.os.Parcelable;
import dagger.Provides;
import de.greenrobot.event.EventBus;
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
import java.util.UUID;

import static name.neuhalfen.todosimple.helper.Preconditions.checkNotNull;

@Layout(R.layout.task_detail_view)
public class DetailScreen implements HasParent<TaskListScreen>, Blueprint {
    static enum State {EXISTING, NEW}
    static final class Cmd {
        public final State cmd;
        public final UUID taskId;

        private Cmd(State cmd, UUID taskId) {
            this.cmd = cmd;
            this.taskId = taskId;
        }
    }

    private final UUID taskId;
    private final State state;

    /*
     * Create
     */
    private DetailScreen() {
        this.state = State.NEW;
        this.taskId = UUID.randomUUID();
    }
    /*
    * edit
     */
    private DetailScreen(UUID taskId) {
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

    public static Blueprint forExistingTask(UUID taskId) {
        checkNotNull(taskId,"TaskId must not be null");
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
            return new Cmd(state,taskId);
        }

    }


    @Singleton
    static class Presenter extends ViewPresenter<DetailView> {
        static class TaskDTO {
            public final int version;
            public final UUID id;
            public final String title;
            public final String description;

            public final State state;

            TaskDTO(UUID id, int version, String title, String description, State state) {
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
        }


        private final TaskManagingApplication taskApp;
        private final ActionBarOwner actionBar;
        private final Parcer<Object> parcer;
        private final EventBus eventBus;
        private final Cmd cmd;


        @Inject
        Presenter(@ForApplication TaskManagingApplication taskApp, @ForApplication EventBus eventBus, ActionBarOwner actionBar, Parcer<Object> parcer, Cmd cmd) {
            this.taskApp = taskApp;
            this.actionBar = actionBar;
            this.parcer = parcer;
            this.eventBus = eventBus;
            this.cmd = cmd;
        }

        TaskDTO loadOrCreateTaskDTO(Cmd cmd) {
           if (cmd.cmd == State.NEW) {
               return new TaskDTO(UUID.randomUUID(), 0,"new task title", "new task description", State.NEW);
           } else {
               final Option<Task> taskOption = taskApp.loadTask(cmd.taskId);
               if (taskOption.isDefined()) {
                   final Task task = taskOption.get();
                   return new TaskDTO(task._aggregateId(), task.version(), "TODO any title", task._description(), State.EXISTING);
               }
               else {
                   throw new IllegalStateException(String.format("Task %s not found",cmd.taskId.toString() ));
               }
           }
        }
        @Override
        public void onLoad(Bundle savedState) {
            super.onLoad(savedState);
            DetailView view = getView();

            if (view != null) {

                ActionBarOwner.Config actionBarConfig = actionBar.getConfig();

                actionBarConfig =
                        actionBarConfig.withAction(new ActionBarOwner.MenuAction("Save", new Action0() {
                            @Override
                            public void call() {
                                saveTask();
                            }
                        }));

                final TaskDTO taskDTO = loadOrCreateTaskDTO(cmd);

                actionBar.setConfig(actionBarConfig.withTitle(taskDTO.description));

                view.setOriginalState(taskDTO);

                if (savedState != null) {
                    final Parcelable parcelable = savedState.getParcelable("edit-state");
                    view.setEditState(null != parcelable ? (TaskDTO) parcer.unwrap(parcelable) : null);
                }
            }
        }

        private void saveTask() {
            DetailView view = getView();
            if (null == view) {
                return;
            }



            try {
                final TaskDTO editState = view.getEditState();

                final Command command;
                switch (editState.state) {
                    case EXISTING:
                        command = new RenameTaskCommand(UUID.randomUUID(), editState.id, editState.version, editState.description);
                        break;
                    case NEW:
                        command = new CreateTaskCommand(UUID.randomUUID(), editState.id, editState.description,0);
                        break;
                    default:
                        throw new IllegalStateException(String.format( "There should be no third state but it is '%s'", editState.state));
                }
                taskApp.executeCommand(command);
            } catch (Exception e) {
                eventBus.post(ViewShowNotificationCommand.makeText(e.getLocalizedMessage(), ViewShowNotificationCommand.Style.ALERT));
            }
        }

        @Override
        public void onSave(Bundle outState) {
            super.onSave(outState);
            final TaskDTO editState = getView().getEditState();
            outState.putParcelable("edit-state", null != editState ? parcer.wrap(editState) : null);
        }

    }
}
