package name.neuhalfen.todosimple.android.infrastructure.db.dbviews.todo;

import name.neuhalfen.todosimple.android.view.label.LabelDTO;
import name.neuhalfen.todosimple.domain.model.TaskId;

import java.util.Set;

public interface LabelsForTaskQueryService {
    Set<LabelDTO> findByTask(TaskId taskId);
}
