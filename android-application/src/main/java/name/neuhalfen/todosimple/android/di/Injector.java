package name.neuhalfen.todosimple.android.di;

public interface Injector {
    <T> T get(Class<? extends T> type);
    <T> void inject(T instance);

}
