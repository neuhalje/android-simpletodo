package name.neuhalfen.todosimple.todosimple.view;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import name.neuhalfen.todosimple.todosimple.R;
import name.neuhalfen.todosimple.todosimple.domain.queries.TodoContentProvider;


/**
 * A fragment representing a single Todo detail screen.
 * This fragment is either contained in a {@link TodoListActivity}
 * in two-pane mode (on tablets) or a {@link TodoDetailActivity}
 * on handsets.
 */
public class TodoDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


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

    private Uri todoUri;

    @InjectView(R.id.todo_edit_title)
    EditText titleText;

    @InjectView(R.id.todo_edit_description)
    EditText descriptionText;

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
            todoUri = null;
        } else {
            todoUri = Uri.parse(uriStr);
        }

        viewState = VIEW_STATE.CREATED;
    }

    private void fillData(Cursor cursor) {

        if (!hasView()) {
            // We might be called while tearing down the activity/fragment
            return;
        }

        final boolean hasData = (cursor != null) && cursor.moveToFirst();

        if (hasData) {
            titleText.setText(cursor.getString(cursor
                    .getColumnIndexOrThrow(TodoContentProvider.TodoTable.COLUMN_TITLE)));
            descriptionText.setText(cursor.getString(cursor
                    .getColumnIndexOrThrow(TodoContentProvider.TodoTable.COLUMN_DESCRIPTION)));
        } else {
            titleText.setText("");
            descriptionText.setText("");
        }
        viewState = VIEW_STATE.BOUND;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Crouton.cancelAllCroutons();
        ButterKnife.reset(this);
        Log.i("BUTTER", String.format("reset: %b" , descriptionText != null));
        viewState = VIEW_STATE.CREATED;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_todo_detail, container, false);
        ButterKnife.inject(this, rootView);
        Log.i("BUTTER", String.format("injected: %b" , descriptionText != null));

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
                    Crouton.makeText(getActivity(), "Task " + todoUri.toString() + " saved.", Style.CONFIRM).show();
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
        String[] projection = {TodoContentProvider.TodoTable.COLUMN_ID, TodoContentProvider.TodoTable.COLUMN_TITLE, TodoContentProvider.TodoTable.COLUMN_DESCRIPTION};
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
        ContentValues values = new ContentValues();
        values.put
                (TodoContentProvider.TodoTable.COLUMN_TITLE, titleText.getText().toString());
        values.put
                (TodoContentProvider.TodoTable.COLUMN_DESCRIPTION, descriptionText.getText().toString());

        if (isEditExistingTask()) {
            getActivity().getContentResolver().update(todoUri, values, null, null);
        } else {
            todoUri = getActivity().getContentResolver().insert(TodoContentProvider.CONTENT_URI, values);
            dataState = DATA_STATE.LOADED;
        }
    }

    private void deleteTask() {
        dataState = DATA_STATE.NO_DATA;
        getActivity().getContentResolver().delete(todoUri, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        dataState = DATA_STATE.LOADED;
        fillData(data);
        Crouton.makeText(getActivity(), "Loaded " + todoUri.toString(), Style.INFO).show();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // data is not available anymore, delete reference
        dataState = DATA_STATE.NO_DATA;
        fillData(null);
    }
}
