package name.neuhalfen.todosimple.android.infrastructure.db.dbviews.todo;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;
import de.greenrobot.event.EventBus;
import name.neuhalfen.todosimple.android.di.ForApplication;
import name.neuhalfen.todosimple.android.di.Injector;
import name.neuhalfen.todosimple.android.infrastructure.db.TodoSQLiteHelper;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TodoContentProviderImpl extends ContentProvider implements TodoContentProvider {

    @Inject
    @ForApplication
    EventBus eventBus;

    // database
    private TodoSQLiteHelper database;

    // used for the UriMacher
    static final int TODOS = 10;
    static final int TODO_AGGREGATE_ID = 30;
    static final int TODO_ID = 20;


    static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, TODOS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", TODO_ID);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/*", TODO_AGGREGATE_ID);
    }

    @Override
    public boolean onCreate() {
        database = new TodoSQLiteHelper(getContext());

        Context applicationContext = getContext().getApplicationContext();
        if (applicationContext instanceof Injector) {
            Injector injector = (Injector) applicationContext;
            injector.inject(this);
            return true;
        } else {
            // DIE!!!!
            Log.wtf("TodoContentProviderImpl", String.format("applicationContext (%s) is no Injector", applicationContext.getClass().toString()));
            // We'll never get here bc/ Log.wtf kills the app.
            return false;
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // Using SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // check if the caller has requested a column which does not exists
        checkColumns(projection);

        // Set the table
        queryBuilder.setTables(TodoTableImpl.TABLE_TODOS);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case TODOS:
                break;
            case TODO_ID:
                // adding the ID to the original query
                // FIXME: SQL Injection
                queryBuilder.appendWhere(TodoTable.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            case TODO_AGGREGATE_ID:
                // FIXME: SQL Injection
                queryBuilder.appendWhere(TodoTable.COLUMN_AGGREGATE_ID + "='"
                        + uri.getLastPathSegment() + "'");
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("insert not supported. Use the application.");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("delete not supported. Use the application.");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("update not supported. Use the application.");
    }


    private void checkColumns(String[] projection) {
        if (projection != null) {
            Set<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            Set<String> availableColumns = new HashSet<String>(Arrays.asList(TodoTable.ALL_COLUMNS));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }
}
