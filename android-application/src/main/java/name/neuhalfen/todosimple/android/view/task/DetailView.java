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

import android.content.Context;
import android.util.AttributeSet;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import flow.Parcer;
import mortar.Mortar;
import name.neuhalfen.todosimple.android.R;
import name.neuhalfen.todosimple.android.view.label.LabelDTO;
import name.neuhalfen.todosimple.android.view.label.LabelListView;
import name.neuhalfen.todosimple.domain.model.TaskId;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.UUID;

import static name.neuhalfen.todosimple.helper.Preconditions.checkNotNull;
import static name.neuhalfen.todosimple.helper.Preconditions.checkState;

public class DetailView extends LinearLayout {
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

    private TaskDTO.State taskStaus;

    private int taskVersion;
    private TaskId taskId;


    public DetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) { // IDEA Editor
            return;
        }
        Mortar.inject(context, this);
    }


    @OnClick(R.id.todo_detail_add_label_button)
    void assignNewLabel() {
        final String labelText = labelAutoComplete.getText().toString();

        if (StringUtils.isBlank(labelText)) {return;}

        final UUID id = UUID.randomUUID();
        LabelDTO label = new LabelDTO(id, labelText);
        labels.assignLabel(label);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (isInEditMode()) { // IDEA Editor
            return;
        }
        ButterKnife.inject(this);
        presenter.takeView(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (isInEditMode()) { // IDEA Editor
            return;
        }
        presenter.onCloseView();
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


    public <T extends ListAdapter & Filterable> void  setAvailableLabelProvider(T adapter){
        labelAutoComplete.setAdapter(adapter);
    }


    public TaskId getTaskId() {
        return taskId;
    }

    public TaskDTO.State getTaskStaus() {
        return taskStaus;
    }

    public void setTaskStaus(TaskDTO.State taskStaus) {
        this.taskStaus = taskStaus;
    }

}
