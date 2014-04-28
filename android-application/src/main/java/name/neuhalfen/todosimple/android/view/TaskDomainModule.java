package name.neuhalfen.todosimple.android.view;

import dagger.Module;
import dagger.Provides;
import name.neuhalfen.myscala.domain.application.TaskManagingApplication;

/**
* Created by jens on 28/04/14.
*/
@Module(injects = TodoListFragment.class)
public class TaskDomainModule {
    @Provides
    TaskManagingApplication provideTaskManagementApplication() {
        return new TaskManagingApplication();
    }
}
