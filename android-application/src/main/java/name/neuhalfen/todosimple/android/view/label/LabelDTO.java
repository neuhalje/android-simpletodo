package name.neuhalfen.todosimple.android.view.label;

import java.util.UUID;

public class LabelDTO {
    public final UUID id;
    public final String name;

    public LabelDTO(UUID id, String name) {
        this.id = id;
        this.name = name;
    }


    public String toString() {
        return name;
    }
}
