package name.neuhalfen.todosimple.android.infrastructure.db.dbviews.label;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import name.neuhalfen.todosimple.android.di.ForApplication;
import name.neuhalfen.todosimple.android.infrastructure.db.TodoSQLiteHelper;
import name.neuhalfen.todosimple.android.view.label.LabelDTO;
import name.neuhalfen.todosimple.domain.model.LabelId;
import scala.Option;

import javax.inject.Inject;

import static name.neuhalfen.todosimple.helper.Preconditions.checkNotNull;

public class LabelQueryServiceImpl implements LabelQueryService {

    @Inject
    @ForApplication
    TodoSQLiteHelper database;

    @Inject
    public LabelQueryServiceImpl() {
    }

    @Override
    public Option<LabelDTO> findByTitle(String labelText) {

        checkNotNull(labelText, "labelText must not be null");

        SQLiteDatabase db = null;

        try {
            db = database.getReadableDatabase();

            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

            // Set the table
            queryBuilder.setTables(LabelTableImpl.TABLE_NAME);
            Cursor cursor = queryBuilder.query(db, new String[]{LabelContentProvider.LabelTable.COLUMN_AGGREGATE_ID, LabelContentProvider.LabelTable.COLUMN_TITLE}, LabelContentProvider.LabelTable.COLUMN_TITLE + "=?", new String[]{labelText}, null, null, null);

            if (cursor.isAfterLast()) {
                return Option.empty();
            } else {
                cursor.moveToFirst();
                final String id = cursor.getString(0);
                final String labelTitle = cursor.getString(1);

                final LabelDTO label = new LabelDTO(LabelId.fromString(id), labelTitle);
                return Option.apply(label);
            }
        } finally {
            if (db != null) {
                db.close();
            }
        }

    }
}
