package name.neuhalfen.todosimple.android.view.task;

import android.os.Bundle;
import android.os.Parcelable;
import dagger.Provides;
import flow.HasParent;
import flow.Layout;
import flow.Parcer;
import mortar.Blueprint;
import mortar.ViewPresenter;
import name.neuhalfen.todosimple.android.R;
import name.neuhalfen.todosimple.android.di.ForApplication;
import name.neuhalfen.todosimple.android.view.base.ActionBarOwner;
import name.neuhalfen.todosimple.android.view.base.Main;
import name.neuhalfen.todosimple.domain.application.TaskManagingApplication;
import name.neuhalfen.todosimple.domain.model.Commands;
import name.neuhalfen.todosimple.domain.model.RenameTaskCommand;
import name.neuhalfen.todosimple.domain.model.Task;
import name.neuhalfen.todosimple.helper.Preconditions;
import rx.util.functions.Action0;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.UUID;

@Layout(R.layout.task_detail_view)
public class DetailScreen implements HasParent<TaskListScreen>, Blueprint {
    private final UUID taskId;

    public DetailScreen(UUID taskId) {
        Preconditions.checkNotNull(taskId, "taskId must not be null");
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

    @dagger.Module(addsTo = Main.Module.class, injects = {DetailView.class
            , DetailScreen.class})
    class Module {
        @Provides
        Task provideTask(@ForApplication TaskManagingApplication taskApp) {
            return taskApp.loadTask(taskId).get();
        }

    }


    @Singleton
    static class Presenter extends ViewPresenter<DetailView> {
        static class TaskDTO {
            public final int version;
            public final UUID id;
            public final String title;
            public final String description;

            TaskDTO(UUID id, int version, String title, String description) {
                this.version = version;
                this.id = id;
                this.title = title;
                this.description = description;
            }

            public TaskDTO withTitle(String newTitle) {
                return new TaskDTO(id, version, newTitle, description);
            }

            public TaskDTO withDescription(String newDescription) {
                return new TaskDTO(id, version, title, newDescription);
            }
        }

        private final TaskManagingApplication taskApp;
        private final Task task;
        private final ActionBarOwner actionBar;
        private final Parcer<Object> parcer;

        @Inject
        Presenter(@ForApplication TaskManagingApplication taskApp, Task task, ActionBarOwner actionBar, Parcer<Object> parcer) {
            this.taskApp = taskApp;
            this.task = task;
            this.actionBar = actionBar;
            this.parcer = parcer;
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

                actionBar.setConfig(actionBarConfig.withTitle(task._description()));

                final TaskDTO dto = new TaskDTO(task._aggregateId(), task.version(), "TODO any title", task._description());

                view.setOriginalState(dto);

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

            final TaskDTO editState = view.getEditState();
            final RenameTaskCommand renameTaskCommand = Commands.renameTask(task, editState.description);
            taskApp.executeCommand(renameTaskCommand);
        }

        @Override
        public void onSave(Bundle outState) {
            super.onSave(outState);
            final TaskDTO editState = getView().getEditState();
            outState.putParcelable("edit-state", null != editState ? parcer.wrap(editState) : null);
        }

    }
}
