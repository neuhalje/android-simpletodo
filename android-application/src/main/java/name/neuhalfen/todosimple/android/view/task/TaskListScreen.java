package name.neuhalfen.todosimple.android.view.task;

import android.os.Bundle;
import flow.Flow;
import flow.Layout;
import mortar.Blueprint;
import mortar.ViewPresenter;
import name.neuhalfen.todosimple.android.R;
import name.neuhalfen.todosimple.android.di.ForApplication;
import name.neuhalfen.todosimple.android.view.base.ActionBarOwner;
import name.neuhalfen.todosimple.android.view.base.Main;
import name.neuhalfen.todosimple.domain.application.TaskManagingApplication;
import name.neuhalfen.todosimple.domain.model.Commands;
import name.neuhalfen.todosimple.domain.model.CreateTaskCommand;
import rx.util.functions.Action0;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.UUID;

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
        /*
        @Provides
        List<String> provideConversations(Chats chats) {
            return chats.getAll();
        }
        */
    }

    @Singleton
    public static class Presenter extends ViewPresenter<TaskListView> {
        private final Flow flow;
        private final ActionBarOwner actionBar;
        private final TaskManagingApplication taskApp;

        @Inject
        Presenter(Flow flow, ActionBarOwner actionBar, @ForApplication TaskManagingApplication taskApp) {
            this.flow = flow;
            this.actionBar = actionBar;
            this.taskApp = taskApp;
        }

        @Override
        public void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            TaskListView view = getView();
            if (view == null) return;

            ActionBarOwner.Config actionBarConfig = actionBar.getConfig();

            actionBarConfig =
                    actionBarConfig.withAction(new ActionBarOwner.MenuAction("Create Demo Tasks", new Action0() {
                        @Override
                        public void call() {
                            for (int i = 1; i < 500; i++) {
                                CreateTaskCommand createTaskCommand = Commands.createTask(String.format("Todo #%0,10d", i));
                                taskApp.executeCommand(createTaskCommand);
                                TaskListView view = getView();
                                if (view == null) return;
                                view.reloadQuery();
                            }

                        }
                    }));

            actionBar.setConfig(actionBarConfig);
            view.showTasks();
        }


        public void onTaskSelected(UUID taskID) {
            flow.goTo(new DetailScreen(taskID));
        }
    }
}

