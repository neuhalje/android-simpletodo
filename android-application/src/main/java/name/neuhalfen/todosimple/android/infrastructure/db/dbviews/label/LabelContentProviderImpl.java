/*
Copyright 2014 Jens Neuhalfen

Licensed under the Apache License, Version 2.0 (the "License"); you may not use
this file except in compliance with the License. You may obtain a copy of the
License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed
under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
 */
package name.neuhalfen.todosimple.android.infrastructure.db.dbviews.label;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;
import name.neuhalfen.todosimple.android.di.Injector;
import name.neuhalfen.todosimple.android.infrastructure.db.TodoSQLiteHelper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LabelContentProviderImpl extends ContentProvider implements LabelContentProvider {

    // database
    private TodoSQLiteHelper database;

    // used for the UriMacher
    static final int LABELS = 10;
    static final int LABEL_AGGREGATE_ID = 30;
    static final int LABEL_ID = 20;


    static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, LABELS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", LABEL_ID);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/*", LABEL_AGGREGATE_ID);
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
            Log.wtf("LabelContentProviderImpl", String.format("applicationContext (%s) is no Injector", applicationContext.getClass().toString()));
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
        queryBuilder.setTables(LabelTableImpl.TABLE_NAME);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case LABELS:
                break;
            case LABEL_ID:
                // adding the ID to the original query
                queryBuilder.appendWhere(LabelTable.COLUMN_ID + "="
                        + Integer.parseInt(uri.getLastPathSegment()));
                break;
            case LABEL_AGGREGATE_ID:
                queryBuilder.appendWhereEscapeString(LabelTable.COLUMN_AGGREGATE_ID + "='"
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
            Set<String> availableColumns = new HashSet<String>(Arrays.asList(LabelTable.ALL_COLUMNS));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }
}
