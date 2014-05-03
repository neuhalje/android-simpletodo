package name.neuhalfen.todosimple.android.view;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import name.neuhalfen.todosimple.android.R;
import name.neuhalfen.todosimple.android.di.DIListFragment;
import name.neuhalfen.todosimple.android.di.ForApplication;
import name.neuhalfen.todosimple.android.infrastructure.db.dbviews.todo.TodoContentProvider;
import name.neuhalfen.todosimple.android.infrastructure.db.dbviews.todo.TodoContentProvider.TodoTable;
import name.neuhalfen.todosimple.domain.application.TaskManagingApplication;
import name.neuhalfen.todosimple.domain.model.Commands;
import name.neuhalfen.todosimple.domain.model.CreateTaskCommand;
import name.neuhalfen.todosimple.domain.model.TaskDeletedEvent;

import javax.inject.Inject;
import java.util.UUID;


/**
 * A list fragment representing a list of Todos. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link TodoDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class TodoListFragment extends DIListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {


    @Inject
    @ForApplication
    TaskManagingApplication taskApp;


    @Inject
    @ForApplication
    EventBus eventBus;

    private SimpleCursorAdapter adapter;

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         *
         * @param id
         */
        public void onItemSelected(Uri id);

        void onCreateNewTask();
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(Uri id) {
        }

        @Override
        public void onCreateNewTask() {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TodoListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // default, but important: setting retainInstance to true
        // breaks querying after a task is deleted.
        setRetainInstance(false);

        setListAdapter(createDbAdapter());
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_list_actionbar, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.add_new_task:
                mCallbacks.onCreateNewTask();
                return true;
            case R.id.add_demo_items:

                for (int i = 1; i < 500; i++) {
                    CreateTaskCommand createTaskCommand = Commands.createTask(String.format("Todo #%0,10d", i));
                    taskApp.executeCommand(createTaskCommand);
                }
                getLoaderManager().restartLoader(0, null, this);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private SimpleCursorAdapter createDbAdapter() {

        // Fields from the database (projection)
        // Must include the _id column for the adapter to work
        String[] from = new String[]{TodoTable.COLUMN_TITLE};
        // Fields on the UI to which we map
        int[] to = new int[]{android.R.id.text1};

        adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_activated_1, null, from,
                to, 0);

        return adapter;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        eventBus.register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
        eventBus.unregister(this);
        Crouton.cancelAllCroutons();
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.

        Cursor cursor = (Cursor) listView.getAdapter().getItem(position);
        String aggregateId = cursor.getString(cursor.getColumnIndex(TodoTable.COLUMN_AGGREGATE_ID));
        Uri todoUri = TodoContentProvider.Factory.forAggregateId(UUID.fromString(aggregateId));

        mCallbacks.onItemSelected(todoUri);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }


    // creates a new loader after the initLoader () call
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {TodoTable.COLUMN_ID, TodoTable.COLUMN_TITLE, TodoTable.COLUMN_AGGREGATE_ID};
        ;
        CursorLoader cursorLoader = new CursorLoader(getActivity(),
                TodoContentProvider.CONTENT_URI, projection, null, null, TodoTable.COLUMN_TITLE);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // data is not available anymore, delete reference
        adapter.swapCursor(null);
    }

    /*
     * Events
     */

    /**
     * Called by the event bus
     *
     * @param event
     */
    public void onEventMainThread(TaskDeletedEvent event) {

        Activity activity = getActivity();
        if (null != activity) {
            Crouton.makeText(activity, "Task deleted.", Style.INFO).show(); // <-- This crouton is not shown (or only for fractions of a second). bug?
            Toast.makeText(activity, "Task deleted", Toast.LENGTH_SHORT).show();
        } else {
            Log.w("ListFragment", "Got delete event but no activity");
        }
    }

}
