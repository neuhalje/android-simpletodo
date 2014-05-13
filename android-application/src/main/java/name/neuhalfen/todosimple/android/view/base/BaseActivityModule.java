package name.neuhalfen.todosimple.android.view.base;

import dagger.Module;
import name.neuhalfen.todosimple.android.di.AndroidApplicationModule;
import name.neuhalfen.todosimple.android.di.EventBusModule;

@Module(injects = BaseActivity.class, includes = {EventBusModule.class, ActionBarModule.class}, addsTo = AndroidApplicationModule.class)
public class BaseActivityModule {
}
