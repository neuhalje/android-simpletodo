package name.neuhalfen.todosimple.android.infrastructure.db.dbviews.label;

import name.neuhalfen.todosimple.android.view.label.LabelDTO;
import scala.Option;

public interface LabelQueryService {
    Option<LabelDTO> findByTitle(String labelText);
}
