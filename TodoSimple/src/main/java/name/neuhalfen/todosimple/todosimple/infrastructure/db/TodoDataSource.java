package name.neuhalfen.todosimple.todosimple.infrastructure.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import name.neuhalfen.todosimple.todosimple.domain.model.Todo;

import java.util.ArrayList;
import java.util.List;

public class TodoDataSource {
    // Database fields
    private SQLiteDatabase database;
    private TodoSQLiteHelper dbHelper;
    private String[] allColumns = {TodoSQLiteHelper.COLUMN_ID,
            TodoSQLiteHelper.COLUMN_TODO};

    public TodoDataSource(Context context) {
        dbHelper = new TodoSQLiteHelper(context);
    }

    public TodoDataSource(TodoSQLiteHelper helper) {
        dbHelper = helper;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Todo createTodo(String comment) {
        ContentValues values = new ContentValues();
        values.put(TodoSQLiteHelper.COLUMN_TODO, comment);
        long insertId = database.insert(TodoSQLiteHelper.TABLE_TODOS, null,
                values);
        Cursor cursor = database.query(TodoSQLiteHelper.TABLE_TODOS,
                allColumns, TodoSQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Todo newTodo = cursorToTodo(cursor);
        cursor.close();
        return newTodo;
    }

    public void deleteTodo(Todo comment) {
        long id = comment.getId();
        System.out.println("Todo deleted with id: " + id);
        database.delete(TodoSQLiteHelper.TABLE_TODOS, TodoSQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Todo> getAllTodos() {
        List<Todo> comments = new ArrayList<Todo>();

        Cursor cursor = database.query(TodoSQLiteHelper.TABLE_TODOS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Todo comment = cursorToTodo(cursor);
            comments.add(comment);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return comments;
    }

    private Todo cursorToTodo(Cursor cursor) {
        Todo comment = new Todo();
        comment.setId(cursor.getLong(0));
        comment.setTodo(cursor.getString(1));
        return comment;
    }

    public Todo findById(long id) {
        Cursor cursor = database.query(TodoSQLiteHelper.TABLE_TODOS,
                allColumns, TodoSQLiteHelper.COLUMN_ID
                        + " = " + id, null, null, null, null
        );

        final Todo todo;
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            todo = cursorToTodo(cursor);
        } else {
            todo = null;
        }
        // make sure to close the cursor
        cursor.close();
        return todo;
    }
}
