package name.neuhalfen.todosimple.android.view;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import de.greenrobot.event.EventBus;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import name.neuhalfen.todosimple.android.R;
import name.neuhalfen.todosimple.android.di.DIActivity;
import name.neuhalfen.todosimple.android.di.ForApplication;
import name.neuhalfen.todosimple.android.domain.model.TodoDeletedEvent;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;


/**
 * An activity representing a list of Todos. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link TodoDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link TodoListFragment} and the item details
 * (if present) is a {@link TodoDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link TodoListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class TodoListActivity extends DIActivity
        implements TodoListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Inject
    @ForApplication
    EventBus eventBus;


    @CheckForNull
    private TodoDetailFragment detailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);

        if (findViewById(R.id.todo_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            FragmentManager fm = getFragmentManager();


            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            final TodoListFragment listFragment = (TodoListFragment) fm
                    .findFragmentById(R.id.todo_list);
            listFragment
                    .setActivateOnItemClick(true);

            // Restoring the detail fragment, when it exists
            // Important: The fragment has to call setRetainInstance() in onCreate
            detailFragment = (TodoDetailFragment) fm.findFragmentById(R.id.todo_detail_container);
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }

    /**
     * Callback method from {@link TodoListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     *
     * @param id
     */
    @Override
    public void onItemSelected(Uri id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(TodoDetailFragment.ARG_ITEM_URI, id.toString());
            detailFragment = new TodoDetailFragment();
            detailFragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.todo_detail_container, detailFragment)
                    .commit();
        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, TodoDetailActivity.class);
            detailIntent.putExtra(TodoDetailFragment.ARG_ITEM_URI, id.toString());
            startActivity(detailIntent);
        }
    }

    @Override
    public void onCreateNewTask() {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(TodoDetailFragment.ARG_ITEM_URI, TodoDetailFragment.ARG_ITEM_URI__FOR_NEW_TASK);
            detailFragment = new TodoDetailFragment();
            detailFragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.todo_detail_container, detailFragment)
                    .commit();
        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, TodoDetailActivity.class);
            detailIntent.putExtra(TodoDetailFragment.ARG_ITEM_URI, TodoDetailFragment.ARG_ITEM_URI__FOR_NEW_TASK);
            startActivity(detailIntent);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        eventBus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        eventBus.unregister(this);
    }

    @Override
    protected List<Object> getModules() {
        List<Object> modules = new ArrayList<Object>();
        modules.addAll(super.getModules());
        modules.add(new TodoViewModule());
        return modules;
    }

    /**
     * Called by the event bus
     *
     * @param event
     */
    public void onEventMainThread(TodoDeletedEvent event) {
        if (mTwoPane) {
            Log.d("ListFragment", "onTodoDeleted called");
            final boolean hasFragment = (null != detailFragment);

            if (hasFragment) {
                getFragmentManager().beginTransaction().remove(detailFragment)
                        .commit();
            }
        } else {
            Log.e("ListFragment", "onTodoDeleted called, though it should not");
        }
    }
}
