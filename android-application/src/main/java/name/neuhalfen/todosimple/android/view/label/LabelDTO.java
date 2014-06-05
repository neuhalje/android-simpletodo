package name.neuhalfen.todosimple.android.view.label;

import java.util.UUID;

public class LabelDTO {
    public final UUID id;
    public final String name;

    public LabelDTO(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LabelDTO labelDTO = (LabelDTO) o;

        if (id != null ? !id.equals(labelDTO.id) : labelDTO.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public String toString() {
        return name;
    }
}
