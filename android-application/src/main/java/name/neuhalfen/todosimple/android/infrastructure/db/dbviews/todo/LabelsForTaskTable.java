package name.neuhalfen.todosimple.android.infrastructure.db.dbviews.todo;

public interface LabelsForTaskTable {

    /**
     * UUID
     */
    public static final String COLUMN_TASK_AGGREGATE_ID = "task_id";

    /**
     * UUID
     */
    public static final String COLUMN_LABEL_ID = "label_id";

    public static String[] ALL_COLUMNS = {COLUMN_TASK_AGGREGATE_ID,COLUMN_LABEL_ID};

    public final String TABLE_NAME="labels_for_task";

    int TABLE_VERSION = 1;
}
