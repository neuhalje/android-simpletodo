package name.neuhalfen.todosimple.android.view.task;

import name.neuhalfen.todosimple.android.view.label.LabelDTO;
import name.neuhalfen.todosimple.domain.model.TaskId;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

class TaskDTO {
    public static enum State {EXISTING, NEW, DELETED}

    public final int version;
    public final TaskId id;
    public final String title;
    public final String description;

    public final Set<LabelDTO> assignedLabels;

    public final State state;

    TaskDTO(TaskId id, int version, String title, String description, Set<LabelDTO> assignedLabels, State state) {
        this.version = version;
        this.id = id;
        this.title = title;
        this.description = description;
        this.assignedLabels = Collections.unmodifiableSet(assignedLabels);
        this.state = state;
    }

    public TaskDTO withTitle(String newTitle) {
        return new TaskDTO(id, version, newTitle, description, assignedLabels, state);
    }

    public TaskDTO withDescription(String newDescription) {
        return new TaskDTO(id, version, title, newDescription, assignedLabels, state);
    }

    public TaskDTO asSaved() {
        return new TaskDTO(id, version, title, description, assignedLabels, State.EXISTING);
    }

    public TaskDTO withVersion(int newVersion) {
        return new TaskDTO(id, newVersion, title, description, assignedLabels, state);
    }

    public TaskDTO withLabel(LabelDTO newLabel) {
        if (assignedLabels.contains(newLabel)) {
            return this;
        }
        Set<LabelDTO> newAssignedLabels = new HashSet<LabelDTO>(assignedLabels);
        newAssignedLabels.add(newLabel);

        return new TaskDTO(id, version, title, description, newAssignedLabels, state);
    }

    public TaskDTO withoutLabel(LabelDTO newLabel) {
        if (!assignedLabels.contains(newLabel)) {
            return this;
        }
        Set<LabelDTO> newAssignedLabels = new HashSet<LabelDTO>(assignedLabels);
        newAssignedLabels.remove(newLabel);

        return new TaskDTO(id, version, title, description, newAssignedLabels, state);
    }
}
