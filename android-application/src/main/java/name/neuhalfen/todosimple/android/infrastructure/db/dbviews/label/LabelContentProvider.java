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

import android.content.ContentResolver;
import android.net.Uri;
import name.neuhalfen.todosimple.domain.model.Label;
import name.neuhalfen.todosimple.domain.model.UniqueId;

/**
 * Content provider (http://developer.android.com/guide/topics/providers/content-providers.html) interface
 * implemented in the infrastructure (TodoContentProviderImpl).
 */
public interface LabelContentProvider {
    public interface LabelTable {
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

        public static String[] ALL_COLUMNS = {LabelTable.COLUMN_AGGREGATE_ID, LabelTable.COLUMN_AGGREGATE_VERSION, LabelTable.COLUMN_TITLE,
                LabelTable.COLUMN_ID};

        int TABLE_VERSION = 1;
    }

    public static class Factory {
        public static Uri forAggregateId(UniqueId<Label> aggregateId) {
            return Uri.withAppendedPath(CONTENT_URI, aggregateId.toString());
        }

        public static Uri forContentProvider_Id(long _id) {
            return Uri.withAppendedPath(CONTENT_URI, "" + _id);
        }

        private Factory() {
        }
    }


    public static final String AUTHORITY = "name.neuhalfen.todosimple.android.TODOS";

    static final String BASE_PATH = "labels";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/labels";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/label";

}
