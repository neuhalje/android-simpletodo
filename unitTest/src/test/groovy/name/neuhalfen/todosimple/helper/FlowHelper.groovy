package name.neuhalfen.todosimple.helper

import android.view.View
import name.neuhalfen.todosimple.android.R


class FlowHelper {
    static View currentFlowViewFor(activity) {
        //TODO: not found!        def view =  activity.findViewById(R.layout.task_list_view)

        activity.findViewById(R.id.container).getChildAt(0)
    }
}
