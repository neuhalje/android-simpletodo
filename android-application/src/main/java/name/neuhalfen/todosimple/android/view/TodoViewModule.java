package name.neuhalfen.todosimple.android.view;

import dagger.Module;
import name.neuhalfen.todosimple.android.di.AndroidApplicationModule;

@Module(
        injects = {
                TodoListFragment.class, TodoDetailFragment.class,
                TodoListActivity.class, TodoDetailActivity.class
        },
        addsTo = AndroidApplicationModule.class,
        library = false, complete = true
)
public class TodoViewModule {
}
