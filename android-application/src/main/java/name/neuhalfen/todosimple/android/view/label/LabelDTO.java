package name.neuhalfen.todosimple.android.view.label;

import name.neuhalfen.todosimple.domain.model.LabelId;

import static name.neuhalfen.todosimple.helper.Preconditions.checkNotNull;

public class LabelDTO {
    public final LabelId id;
    public final String name;

    public LabelDTO(LabelId id, String name) {
        checkNotNull(id,"id must not be null");
        checkNotNull(name,"name must not be null");
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
