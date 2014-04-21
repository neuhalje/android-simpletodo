package name.neuhalfen.todosimple.todosimple;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import name.neuhalfen.todosimple.todosimple.infrastructure.db.TodoTable;


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

    TextView todoDetailText;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TodoDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_URI)) {
            String uristr = getArguments().getString(ARG_ITEM_URI);
            todoUri = Uri.parse(uristr);
        }
    }

    private void fillData(Cursor cursor) {
        if (cursor != null) {
            cursor.moveToFirst();

            todoDetailText.setText(cursor.getString(cursor
                    .getColumnIndexOrThrow(TodoTable.COLUMN_TODO)));
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_todo_detail, container, false);

        todoDetailText = (TextView) rootView.findViewById(R.id.todo_detail);
        getLoaderManager().initLoader(0, null, this);

        return rootView;
    }

    // creates a new loader after the initLoader () call
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {TodoTable.COLUMN_ID, TodoTable.COLUMN_TODO};
        CursorLoader cursorLoader = new CursorLoader(getActivity(),
                todoUri, projection, null, null, null);
        return cursorLoader;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        fillData(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // data is not available anymore, delete reference
        fillData(null);
    }
}
