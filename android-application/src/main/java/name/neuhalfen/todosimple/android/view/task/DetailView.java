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
package name.neuhalfen.todosimple.android.view.task;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import flow.Parcer;
import mortar.Mortar;
import name.neuhalfen.todosimple.android.R;
import name.neuhalfen.todosimple.android.infrastructure.db.dbviews.label.LabelContentProvider;
import name.neuhalfen.todosimple.android.view.base.BaseActivity;
import name.neuhalfen.todosimple.android.view.label.LabelDTO;
import name.neuhalfen.todosimple.android.view.label.LabelListView;
import name.neuhalfen.todosimple.domain.model.TaskId;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;

import static name.neuhalfen.todosimple.helper.Preconditions.checkNotNull;
import static name.neuhalfen.todosimple.helper.Preconditions.checkState;

public class DetailView extends RelativeLayout   implements LabelListView.OnLabelClickedListener,  LoaderManager.LoaderCallbacks<Cursor>  {
    @Inject
    DetailScreen.Presenter presenter;

    @Inject
    Parcer<Object> parcer;

    @InjectView(R.id.todo_edit_title)
    EditText editTitle;

    @InjectView(R.id.todo_edit_description)
    EditText editDescription;

    @InjectView(R.id.todo_detail_uuid)
    TextView showUUID;

    @InjectView(R.id.todo_detail_version)
    TextView showVersion;

    @InjectView(R.id.todo_detail_labels)
    LabelListView labels;

    @InjectView(R.id.todo_detail_add_label_button)
    Button addLabelButton;

    @InjectView(R.id.todo_detail_add_label)
    AutoCompleteTextView labelAutoComplete;

    private TaskDTO.State taskStatus;

    private int taskVersion;
    private TaskId taskId;

    private SimpleCursorAdapter adapter;

    public DetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) { // IDEA Editor
            return;
        }
        Mortar.inject(context, this);
    }


    @OnClick(R.id.todo_detail_add_label_button)
    void addLabelButtonClick() {
        final String labelText = labelAutoComplete.getText().toString();
        if (StringUtils.isBlank(labelText)) {return;}
        presenter.onAddLabelClicked(labelText);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (isInEditMode()) { // IDEA Editor
            return;
        }
        ButterKnife.inject(this);

        labels.setOnLabelClickedListener(this);
        presenter.takeView(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (isInEditMode()) { // IDEA Editor
            return;
        }
        presenter.onCloseView();
        labels.setOnLabelClickedListener(null);

        final LoaderManager loaderManager = getLoaderManager();
        if (loaderManager != null) {
            loaderManager.destroyLoader(1);
        }

        ButterKnife.reset(this);
        presenter.dropView(this);
    }

    /*
     *  Display logic
     */

    public void setEditedTitle(String title) {
        checkNotNull(title, "title must not be null");
        checkState(null != editTitle, "editTitle not injected");
        editTitle.setText(title);
    }

    public String getEditedTitle() {
        checkState(null != editTitle, "editTitle not injected");
        return editTitle.getText().toString();
    }

    public void setEditedDescription(String taskDescription) {
        checkNotNull(taskDescription, "taskDescription must not be null");
        checkState(null != editDescription, "editDescription not injected");
        editDescription.setText(taskDescription);
    }

    public String getEditedDescription() {
        checkState(null != editDescription, "editDescription not injected");
        return editDescription.getText().toString();
    }

    public void setTaskVersion(int version) {
        showVersion.setText(String.format("%d", version));
        this.taskVersion = version;
    }

    public int getTaskVersion() {
        return taskVersion;
    }

    public void setTaskId(TaskId id) {
        checkNotNull(id, "id must not be null");
        this.taskId = id;
        showUUID.setText(id.toString());
    }

    public void showLabelAssigned(LabelDTO label ) {
        labels.assignLabel(label);
    }
    public void showLabelRemoved(LabelDTO label ) {
        labels.unassignLabel(label);
    }


    private <T extends ListAdapter & Filterable> void  setAvailableLabelAdapter(T adapter){
        labelAutoComplete.setAdapter(adapter);


    }


    public TaskId getTaskId() {
        return taskId;
    }

    public TaskDTO.State getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskDTO.State taskStatus)
    {
        this.taskStatus = taskStatus;
    }


    @Override
    public void onLabelClicked(LabelListView llv, LabelDTO label) {
        if (presenter!=null) {
            presenter.onRemoveLabelClicked(label);
        }
    }


    //// Label Loader
    @CheckForNull
    private LoaderManager getLoaderManager() {
        Context context = getContext();
        LoaderManager loaderManager = (LoaderManager) context.getSystemService(BaseActivity.NAME_NEUHALFEN_LOADER_MANAGER);

        if (null != loaderManager) {
            return loaderManager;
        } else {
            Log.i("TaskListView", String.format("initLoaderManager: Context '%s' is not returning %s.", context, BaseActivity.NAME_NEUHALFEN_LOADER_MANAGER));
        }
        return null;
    }

    public void fillLabelDropdown() {
        final LoaderManager loaderManager = getLoaderManager();
        adapter = createDbAdapter();
        setAvailableLabelAdapter(adapter);
        if (loaderManager != null) {
            loaderManager.initLoader(1, null, this);
        }
    }

    // creates a new loader after the initLoader () call
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {LabelContentProvider.LabelTable.COLUMN_ID, LabelContentProvider.LabelTable.COLUMN_TITLE, LabelContentProvider.LabelTable.COLUMN_AGGREGATE_ID};
        CursorLoader cursorLoader = new CursorLoader(getContext(),
                LabelContentProvider.CONTENT_URI, projection, null, null, LabelContentProvider.LabelTable.COLUMN_TITLE + " COLLATE LOCALIZED ASC");
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // data is not available anymore, delete reference
        adapter.swapCursor(null);
    }

    private SimpleCursorAdapter createDbAdapter() {

        // Fields from the database (projection)
        // Must include the _id column for the adapter to work
        String[] from = new String[]{LabelContentProvider.LabelTable.COLUMN_TITLE};
        // Fields on the UI to which we map
        int[] to = new int[]{android.R.id.text1};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getContext(), android.R.layout.simple_list_item_activated_1, null, from,
                to, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        adapter.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            @Override
            public CharSequence convertToString(Cursor cursor) {
                final int colIndex = cursor.getColumnIndexOrThrow(LabelContentProvider.LabelTable.COLUMN_TITLE);
                return cursor.getString(colIndex);
            }
        });

        return adapter;
    }
}
