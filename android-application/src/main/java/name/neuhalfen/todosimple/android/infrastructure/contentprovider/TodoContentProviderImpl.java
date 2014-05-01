package name.neuhalfen.todosimple.android.infrastructure.contentprovider;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import de.greenrobot.event.EventBus;
import name.neuhalfen.todosimple.android.di.ForApplication;
import name.neuhalfen.todosimple.android.di.Injector;
import name.neuhalfen.todosimple.android.domain.model.TodoDeletedEvent;
import name.neuhalfen.todosimple.android.domain.queries.TodoContentProvider;
import name.neuhalfen.todosimple.android.infrastructure.db.TodoSQLiteHelper;
import name.neuhalfen.todosimple.android.infrastructure.db.TodoTableImpl;

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
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case TODOS:
                id = sqlDB.insert(TodoTableImpl.TABLE_TODOS, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        Uri newUri = ContentUris.withAppendedId(CONTENT_URI, id);
        getContext().getContentResolver().notifyChange(newUri, null);
        return newUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case TODOS:
                rowsDeleted = sqlDB.delete(TodoTableImpl.TABLE_TODOS, selection,
                        selectionArgs);
                break;
            case TODO_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    // FIXME: SQL Injection
                    rowsDeleted = sqlDB.delete(TodoTableImpl.TABLE_TODOS,
                            TodoTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    // FIXME: SQL Injection
                    rowsDeleted = sqlDB.delete(TodoTableImpl.TABLE_TODOS,
                            TodoTable.COLUMN_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs
                    );
                }

                eventBus.post(new TodoDeletedEvent());
                break;
            case TODO_AGGREGATE_ID:
                String aggregateId = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    // FIXME: SQL Injection
                    rowsDeleted = sqlDB.delete(TodoTableImpl.TABLE_TODOS,
                            TodoTable.COLUMN_AGGREGATE_ID + "='" + aggregateId + "'",
                            null);
                } else {
                    // FIXME: SQL Injection
                    rowsDeleted = sqlDB.delete(TodoTableImpl.TABLE_TODOS,
                            TodoTable.COLUMN_AGGREGATE_ID + "='" + aggregateId + "'"
                                    + " and " + selection,
                            selectionArgs
                    );
                }

                eventBus.post(new TodoDeletedEvent());
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case TODOS:
                rowsUpdated = sqlDB.update(TodoTableImpl.TABLE_TODOS,
                        values,
                        selection,
                        selectionArgs);
                break;
            case TODO_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    // FIXME: SQL Injection
                    rowsUpdated = sqlDB.update(TodoTableImpl.TABLE_TODOS,
                            values,
                            TodoTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    // FIXME: SQL Injection
                    rowsUpdated = sqlDB.update(TodoTableImpl.TABLE_TODOS,
                            values,
                            TodoTable.COLUMN_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs
                    );
                }
                break;
            case TODO_AGGREGATE_ID:
                String aggregateId = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    // FIXME: SQL Injection
                    rowsUpdated = sqlDB.update(TodoTableImpl.TABLE_TODOS,
                            values,
                            TodoTable.COLUMN_AGGREGATE_ID + "='" + aggregateId + "'",
                            null);
                } else {
                    // FIXME: SQL Injection
                    rowsUpdated = sqlDB.update(TodoTableImpl.TABLE_TODOS,
                            values,
                            TodoTable.COLUMN_AGGREGATE_ID + "='" + aggregateId + "'"
                                    + " and "
                                    + selection,
                            selectionArgs
                    );
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
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
