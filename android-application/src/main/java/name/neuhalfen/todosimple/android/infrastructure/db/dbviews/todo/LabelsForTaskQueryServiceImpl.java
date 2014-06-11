package name.neuhalfen.todosimple.android.infrastructure.db.dbviews.todo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import name.neuhalfen.todosimple.android.di.ForApplication;
import name.neuhalfen.todosimple.android.infrastructure.db.TodoSQLiteHelper;
import name.neuhalfen.todosimple.android.infrastructure.db.dbviews.label.LabelContentProvider;
import name.neuhalfen.todosimple.android.view.label.LabelDTO;
import name.neuhalfen.todosimple.domain.model.LabelId;
import name.neuhalfen.todosimple.domain.model.TaskId;

import javax.inject.Inject;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static name.neuhalfen.todosimple.helper.Preconditions.checkNotNull;

public class LabelsForTaskQueryServiceImpl implements LabelsForTaskQueryService {

    final static String SQL_LABEL_DTOS_FOR_TASK_ = "SELECT " + LabelsForTaskTable.COLUMN_LABEL_ID + "," + LabelContentProvider.LabelTable.COLUMN_TITLE + " FROM " + LabelsForTaskTable.TABLE_NAME + " INNER JOIN " + LabelContentProvider.LabelTable.TABLE_NAME + " ON " + LabelsForTaskTable.COLUMN_LABEL_ID + " = " + LabelContentProvider.LabelTable.COLUMN_AGGREGATE_ID + " WHERE " + LabelsForTaskTable.COLUMN_TASK_AGGREGATE_ID + " = ?";

    @Inject
    @ForApplication
    TodoSQLiteHelper database;

    @Inject
    public LabelsForTaskQueryServiceImpl() {
    }

    @Override
    public Set<LabelDTO> findByTask(TaskId taskId) {

        checkNotNull(taskId, "taskId must not be null");

        SQLiteDatabase db = null;

        try {
            db = database.getReadableDatabase();

            Cursor cursor = db.rawQuery(SQL_LABEL_DTOS_FOR_TASK_, new String[]{taskId.toString()});

            if (cursor.isAfterLast()) {
                return Collections.emptySet();
            } else {
                cursor.moveToFirst();
                Set<LabelDTO> set = new HashSet<LabelDTO>();

                while (!cursor.isAfterLast()) {
                    final String labelId = cursor.getString(0);
                    final String labelTitle = cursor.getString(1);
                    final LabelDTO label = new LabelDTO(LabelId.fromString(labelId), labelTitle);

                    set.add(label);
                }
                return set;
            }
        } finally {
            if (db != null) {
                db.close();
            }
        }

    }
}
