package name.neuhalfen.todosimple.todosimple.domain.queries;

import android.content.ContentResolver;
import android.net.Uri;

/**
 * Content provider (http://developer.android.com/guide/topics/providers/content-providers.html) interface
 * implemented in the infrastructure (TodoContentProviderImpl).
 */
public interface TodoContentProvider {
    public interface TodoTable {
        public static final String TABLE_TODOS = "todos";
        public static final String COLUMN_ID = "_id";
        /**
         * String
         */
        public static final String COLUMN_TITLE = "title";
        /**
         * String
         */
        public static final String COLUMN_DESCRIPTION = "description";
    }


    public static final String AUTHORITY = "name.neuhalfen.todosimple.todosimple.TODOS";

    static final String BASE_PATH = "todos";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/todos";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/todo";

}
