package name.neuhalfen.todosimple.android.view.task;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import mortar.Mortar;
import name.neuhalfen.todosimple.android.infrastructure.db.dbviews.todo.TodoContentProvider;
import name.neuhalfen.todosimple.android.view.base.BaseActivity;

import javax.inject.Inject;
import java.util.UUID;

public class TaskListView
        extends ListView implements LoaderManager.LoaderCallbacks<Cursor> {


    @Inject
    TaskListScreen.Presenter presenter;


    private SimpleCursorAdapter adapter;

    public TaskListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Mortar.inject(context, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        presenter.takeView(this);


        setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null == adapter) {
                    return;
                }
                Cursor cursor = (Cursor) adapter.getItem(position);
                if (null == cursor) {
                    return;
                }
                String aggregateId = cursor.getString(cursor.getColumnIndex(TodoContentProvider.TodoTable.COLUMN_AGGREGATE_ID));
                presenter.onTaskSelected(UUID.fromString(aggregateId));
            }
        });
    }


    @CheckForNull
    private LoaderManager getLoaderManager() {
        Context context = getContext();
        LoaderManager loaderManager = (LoaderManager) context.getSystemService(BaseActivity.NAME_NEUHALFEN_LOADER_MANAGER);

        if (null != loaderManager) {
            return loaderManager;
        } else {
            Log.i("TaskListView", String.format("initLoaderManager: Context '%s' is not returning %s.", context, BaseActivity.NAME_NEUHALFEN_LOADER_MANAGER));
        }
        return null;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
        final LoaderManager loaderManager = getLoaderManager();
        if (loaderManager != null) {
            loaderManager.destroyLoader(0);
        }
    }


    public void showTasks() {
        final LoaderManager loaderManager = getLoaderManager();
        adapter = createDbAdapter();
        setAdapter(adapter);
        if (loaderManager != null) {
            loaderManager.initLoader(0, null, this);
        }
    }

    // creates a new loader after the initLoader () call
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {TodoContentProvider.TodoTable.COLUMN_ID, TodoContentProvider.TodoTable.COLUMN_TITLE, TodoContentProvider.TodoTable.COLUMN_AGGREGATE_ID};
        CursorLoader cursorLoader = new CursorLoader(getContext(),
                TodoContentProvider.CONTENT_URI, projection, null, null, TodoContentProvider.TodoTable.COLUMN_TITLE);
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

    private SimpleCursorAdapter createDbAdapter() {

        // Fields from the database (projection)
        // Must include the _id column for the adapter to work
        String[] from = new String[]{TodoContentProvider.TodoTable.COLUMN_TITLE};
        // Fields on the UI to which we map
        int[] to = new int[]{android.R.id.text1};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getContext(), android.R.layout.simple_list_item_activated_1, null, from,
                to, 0);

        return adapter;
    }

    public void reloadQuery() {
        final LoaderManager loaderManager = getLoaderManager();
        if (null != loaderManager) {
            loaderManager.restartLoader(0, null, this);
        }
    }
}
