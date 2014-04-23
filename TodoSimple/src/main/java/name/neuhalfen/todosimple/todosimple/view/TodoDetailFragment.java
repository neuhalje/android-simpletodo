package name.neuhalfen.todosimple.todosimple.view;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_URI = "item_id";


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

        if (getArguments().containsKey(ARG_ITEM_URI)) {
            String uristr = getArguments().getString(ARG_ITEM_URI);
            todoUri = Uri.parse(uristr);
        }
    }

    private void fillData(Cursor cursor) {
        if (cursor != null) {
            cursor.moveToFirst();

            titleText.setText(cursor.getString(cursor
                    .getColumnIndexOrThrow(TodoContentProvider.TodoTable.COLUMN_TITLE)));
            descriptionText.setText(cursor.getString(cursor
                    .getColumnIndexOrThrow(TodoContentProvider.TodoTable.COLUMN_DESCRIPTION)));
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Crouton.cancelAllCroutons();
        ButterKnife.reset(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_todo_detail, container, false);
        ButterKnife.inject(this, rootView);

        getLoaderManager().initLoader(0, null, this);

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
                saveTask();
                Crouton.makeText(getActivity(), "Task " + todoUri.toString() + " saved.", Style.CONFIRM).show();
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
        saveTask();
    }

    private void saveTask() {
        ContentValues values = new ContentValues();
        values.put
                (TodoContentProvider.TodoTable.COLUMN_TITLE, titleText.getText().toString());
        values.put
                (TodoContentProvider.TodoTable.COLUMN_DESCRIPTION, descriptionText.getText().toString());
        getActivity().getContentResolver().update(todoUri, values, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        fillData(data);
        Crouton.makeText(getActivity(), "Loaded " + todoUri.toString(), Style.INFO).show();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // data is not available anymore, delete reference
        fillData(null);
    }
}
