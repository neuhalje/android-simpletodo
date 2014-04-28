package name.neuhalfen.todosimple.android.services;

import de.greenrobot.event.EventBus;

public class GlobalEventBus {


    private static final EventBus GLOBAL_BUS = new EventBus();

    public static EventBus get() {
        return GLOBAL_BUS;
    }
}
