package name.neuhalfen.todosimple.android.view;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import name.neuhalfen.todosimple.domain.application.TaskManagingApplication;
import name.neuhalfen.todosimple.domain.model.Command;
import name.neuhalfen.todosimple.domain.model.CreateTaskCommand;
import name.neuhalfen.todosimple.domain.model.DeleteTaskCommand;
import name.neuhalfen.todosimple.domain.model.RenameTaskCommand;
import name.neuhalfen.todosimple.android.R;
import name.neuhalfen.todosimple.android.di.DIFragment;
import name.neuhalfen.todosimple.android.di.ForApplication;
import name.neuhalfen.todosimple.android.infrastructure.db.dbviews.todo.TodoContentProvider;

import javax.inject.Inject;
import java.util.UUID;


/**
 * A fragment representing a single Todo detail screen.
 * This fragment is either contained in a {@link TodoListActivity}
 * in two-pane mode (on tablets) or a {@link TodoDetailActivity}
 * on handsets.
 */
public class TodoDetailFragment extends DIFragment implements LoaderManager.LoaderCallbacks<Cursor> {


    @Inject
    @ForApplication
    TaskManagingApplication taskApp;

    private static enum VIEW_STATE {
        UNKNOWN,
        CREATED,
        HAS_VIEW,
        BOUND
    }

    private static enum DATA_STATE {
        NO_DATA, LOADED, NEW_TASK
    }

    private VIEW_STATE viewState = VIEW_STATE.UNKNOWN;
    private DATA_STATE dataState = DATA_STATE.NO_DATA;

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_URI = "item_id";
    /**
     * If the #ARG_ITEM_URI == #ARG_ITEM_URI__FOR_NEW_TASK, then a new todo is created
     */
    public static final String ARG_ITEM_URI__FOR_NEW_TASK = "NEW TASK";

    // FIXME: Handling of the URI is still not good ( uri vs aggregate root id)
    private Uri todoUri;

    @InjectView(R.id.todo_edit_title)
    EditText titleText;

    @InjectView(R.id.todo_edit_description)
    EditText descriptionText;

    @InjectView(R.id.todo_detail_uuid)
    TextView uuidText;

    @InjectView(R.id.todo_detail_uri)
    TextView uriText;

    @InjectView(R.id.todo_detail_version)
    TextView versionText;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TodoDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        // figure out, what we need to do data wise
        dataState = DATA_STATE.NO_DATA;

        final String uriStr = getArguments().getString(ARG_ITEM_URI);
        final boolean isNewTaskUri = ARG_ITEM_URI__FOR_NEW_TASK.equals(uriStr);
        if (isNewTaskUri) {
            updateUri(null);
        } else {
            updateUri(Uri.parse(uriStr));
        }

        viewState = VIEW_STATE.CREATED;
    }

    private void updateUri(Uri uri) {
        this.todoUri = uri;

        if (hasView()) {
            if (null == uri) {
                uriText.setText("N/A");
                Log.i("URI", "none");
            } else {
                final String text = uri.toString();
                uriText.setText(text);
                Log.i("URI", text);
            }
        }

    }


    private void fillData(Cursor cursor) {

        if (!hasView()) {
            // We might be called while tearing down the activity/fragment
            return;
        }

        final boolean hasData = (cursor != null) && cursor.moveToFirst();

        updateUri(todoUri);
        if (hasData) {
            titleText.setText(cursor.getString(cursor
                    .getColumnIndexOrThrow(TodoContentProvider.TodoTable.COLUMN_TITLE)));
            descriptionText.setText(cursor.getString(cursor
                    .getColumnIndexOrThrow(TodoContentProvider.TodoTable.COLUMN_DESCRIPTION)));
            versionText.setText(String.valueOf( cursor.getInt(cursor
                    .getColumnIndexOrThrow(TodoContentProvider.TodoTable.COLUMN_AGGREGATE_VERSION))));
            uuidText.setText(cursor.getString(cursor
                    .getColumnIndexOrThrow(TodoContentProvider.TodoTable.COLUMN_AGGREGATE_ID)));
        } else {
            titleText.setText("");
            descriptionText.setText("");
            versionText.setText("");
            uuidText.setText("");
        }
        viewState = VIEW_STATE.BOUND;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Crouton.cancelAllCroutons();
        ButterKnife.reset(this);
        Log.i("BUTTER", String.format("reset: %b", descriptionText != null));
        viewState = VIEW_STATE.CREATED;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_todo_detail, container, false);
        ButterKnife.inject(this, rootView);
        Log.i("BUTTER", String.format("injected: %b", descriptionText != null));

        if (isEditExistingTask()) {
            getLoaderManager().initLoader(0, null, this);
            viewState = VIEW_STATE.HAS_VIEW;
        } else {
            // TODO: This asymmetry between edit and new is to be resolved..
            dataState = DATA_STATE.NEW_TASK;
            viewState = VIEW_STATE.BOUND;
        }

        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_details_actionbar, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.save_task:
                if (canSaveTask()) {
                    saveTask();
                }
                return true;
            case R.id.delete_task:
                if (canDeleteTask()) {
                    deleteTask();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // creates a new loader after the initLoader () call
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {TodoContentProvider.TodoTable.COLUMN_ID, TodoContentProvider.TodoTable.COLUMN_AGGREGATE_ID,TodoContentProvider.TodoTable.COLUMN_AGGREGATE_VERSION,TodoContentProvider.TodoTable.COLUMN_TITLE, TodoContentProvider.TodoTable.COLUMN_DESCRIPTION};
        CursorLoader cursorLoader = new CursorLoader(getActivity(),
                todoUri, projection, null, null, null);
        return cursorLoader;
    }


    @Override
    public void onStop() {
        super.onStop();
        if (canSaveTask()) {
            saveTask();
        }
    }

    private boolean canDeleteTask() {
        return dataState == DATA_STATE.LOADED;
    }

    private boolean canSaveTask() {
        return (dataState == DATA_STATE.LOADED || dataState == DATA_STATE.NEW_TASK) && (viewState == VIEW_STATE.BOUND);
    }

    private boolean hasView() {
        return viewState == VIEW_STATE.HAS_VIEW || viewState == VIEW_STATE.BOUND;
    }

    private boolean isEditExistingTask() {
        return todoUri != null;
    }

    private void saveTask() {
        final Command cmd;
        if (isEditExistingTask()) {
            cmd = new RenameTaskCommand(UUID.randomUUID(), UUID.fromString( uuidText.getText().toString()) , Integer.parseInt( versionText.getText().toString()), titleText.getText().toString());
        } else {
          cmd = new CreateTaskCommand(UUID.randomUUID(), UUID.randomUUID(), titleText.getText().toString(), 0);
        }
        executeCommand(cmd);
    }

    private void executeCommand(Command cmd) {
        try {
            taskApp.executeCommand(cmd);
            updateUri(TodoContentProvider.Factory.forAggregateId(cmd.aggregateRootId()));
            getLoaderManager().initLoader(0, null, this);
        }catch(Exception e){
            Crouton.makeText(getActivity(), e.toString(), Style.ALERT).show();
        }
    }

    private void deleteTask() {
        final Command cmd = new DeleteTaskCommand(UUID.randomUUID(), UUID.fromString( uuidText.getText().toString()) , Integer.parseInt( versionText.getText().toString()));
        dataState = DATA_STATE.NO_DATA;
        executeCommand(cmd);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        dataState = DATA_STATE.LOADED;
        fillData(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // data is not available anymore, delete reference
        dataState = DATA_STATE.NO_DATA;
        fillData(null);
    }
}
