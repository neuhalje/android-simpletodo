package name.neuhalfen.todosimple.android.view.task;

import name.neuhalfen.todosimple.domain.model.TaskId;

class TaskDTO {
    public static enum State {EXISTING, NEW}

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
