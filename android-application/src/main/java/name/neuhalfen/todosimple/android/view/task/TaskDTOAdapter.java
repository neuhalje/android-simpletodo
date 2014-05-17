package name.neuhalfen.todosimple.android.view.task;

import name.neuhalfen.todosimple.android.di.ForApplication;
import name.neuhalfen.todosimple.domain.application.TaskManagingApplication;
import name.neuhalfen.todosimple.domain.model.Task;
import name.neuhalfen.todosimple.domain.model.TaskId;
import scala.Option;

import javax.inject.Inject;

public class TaskDTOAdapter {
    final TaskManagingApplication taskApp;

    @Inject
    public TaskDTOAdapter(@ForApplication TaskManagingApplication taskApp) {
        this.taskApp = taskApp;
    }


    public TaskDTO loadOrCreateTaskDTO(DetailScreen.Cmd cmd) {
        if (cmd.cmd == TaskDTO.State.NEW) {
            return new TaskDTO(TaskId.generateId(), 0, "", "", TaskDTO.State.NEW);
        } else {
            final Option<Task> taskOption = taskApp.loadTask(cmd.taskId);
            if (taskOption.isDefined()) {
                final Task task = taskOption.get();
                return new TaskDTO(task._aggregateId(), task.version(), task._title(), task._description(), TaskDTO.State.EXISTING);
            } else {
                throw new IllegalStateException(String.format("Task %s not found", cmd.taskId.toString()));
            }
        }
    }
}
