package name.neuhalfen.todosimple.android.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import de.greenrobot.event.EventBus;
import name.neuhalfen.todosimple.android.R;
import name.neuhalfen.todosimple.android.di.DIActivity;
import name.neuhalfen.todosimple.android.di.ForApplication;
import name.neuhalfen.todosimple.domain.model.TaskDeletedEvent;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;


/**
 * An activity representing a single Todo detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link TodoListActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link TodoDetailFragment}.
 */
public class TodoDetailActivity extends DIActivity {

    @Inject
    @ForApplication
    EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_detail);

        // Show the Up button in the action bar.
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(TodoDetailFragment.ARG_ITEM_URI,
                    getIntent().getStringExtra(TodoDetailFragment.ARG_ITEM_URI));
            TodoDetailFragment fragment = new TodoDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .add(R.id.todo_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this, TodoListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
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
    public void onEventMainThread(TaskDeletedEvent event) {
        onBackPressed();
    }

}
