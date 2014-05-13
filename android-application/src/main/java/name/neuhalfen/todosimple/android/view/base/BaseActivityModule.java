package name.neuhalfen.todosimple.android.view.base;

import dagger.Module;
import name.neuhalfen.todosimple.android.di.AndroidApplicationModule;

@Module(injects = BaseActivity.class, includes = { ActionBarModule.class}, addsTo = AndroidApplicationModule.class)
public class BaseActivityModule {
}
