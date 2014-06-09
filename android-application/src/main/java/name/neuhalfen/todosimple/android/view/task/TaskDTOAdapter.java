package name.neuhalfen.todosimple.android.view.task;

import name.neuhalfen.todosimple.android.di.ForApplication;
import name.neuhalfen.todosimple.android.view.label.LabelDTO;
import name.neuhalfen.todosimple.domain.application.TaskManagingApplication;
import name.neuhalfen.todosimple.domain.model.Task;
import scala.Option;

import javax.inject.Inject;
import java.util.HashSet;

public class TaskDTOAdapter {
    final TaskManagingApplication taskApp;

    @Inject
    public TaskDTOAdapter(@ForApplication TaskManagingApplication taskApp) {
        this.taskApp = taskApp;
    }


    public TaskDTO loadOrCreateTaskDTO(DetailScreen.Cmd cmd) {
        if (cmd.cmd == TaskDTO.State.NEW) {
            return new TaskDTO(cmd.taskId, 0, "", "", new HashSet<LabelDTO>(), TaskDTO.State.NEW);
        } else {
            final Option<Task> taskOption = taskApp.loadEntity(cmd.taskId);
            if (taskOption.isDefined()) {
                final Task task = taskOption.get();
                // FIXME: add labels from DB
                return new TaskDTO(task._aggregateId(), task.version(), task._title(), task._description(), new HashSet<LabelDTO>(), TaskDTO.State.EXISTING);
            } else {
                throw new IllegalStateException(String.format("Task %s not found", cmd.taskId.toString()));
            }
        }
    }
}
