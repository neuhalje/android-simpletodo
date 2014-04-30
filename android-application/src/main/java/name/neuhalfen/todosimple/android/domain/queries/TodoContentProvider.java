package name.neuhalfen.todosimple.android.domain.queries;

import android.content.ContentResolver;
import android.net.Uri;

/**
 * Content provider (http://developer.android.com/guide/topics/providers/content-providers.html) interface
 * implemented in the infrastructure (TodoContentProviderImpl).
 */
public interface TodoContentProvider {
    public interface TodoTable {
        public static final String COLUMN_ID = "_id";
        /**
         * UUID
         */
        public static final String COLUMN_AGGREGATE_ID = "aggregate_id";
        /**
         * int
         */
        public static final String COLUMN_AGGREGATE_VERSION = "version";
        /**
         * String
         */
        public static final String COLUMN_TITLE = "title";
        /**
         * String
         */
        public static final String COLUMN_DESCRIPTION = "description";

        public static String[] ALL_COLUMNS = {TodoTable.COLUMN_AGGREGATE_ID, TodoTable.COLUMN_AGGREGATE_VERSION, TodoTable.COLUMN_TITLE,TodoTable.COLUMN_DESCRIPTION,
                TodoTable.COLUMN_ID};

        int TABLE_VERSION = 3;
    }


    public static final String AUTHORITY = "name.neuhalfen.todosimple.android.TODOS";

    static final String BASE_PATH = "todos";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/todos";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/todo";

}
