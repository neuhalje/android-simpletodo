package name.neuhalfen.todosimple.android.view;

import dagger.Module;
import name.neuhalfen.todosimple.android.AndroidApplicationModule;

@Module(
        injects = {
                TodoListFragment.class, TodoListActivity.class
        },
        addsTo = AndroidApplicationModule.class,
        library = true
)
public class TodoListModule {
}
