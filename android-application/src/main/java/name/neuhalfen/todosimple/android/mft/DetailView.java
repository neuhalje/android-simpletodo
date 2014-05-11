package name.neuhalfen.todosimple.android.mft;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import flow.Parcer;
import mortar.Mortar;
import name.neuhalfen.todosimple.android.R;

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

    /**
     * Unmodified state from "creation"(or loading)
     */
    private DetailScreen.Presenter.TaskDTO originalState;
    private final static DetailScreen.Presenter.TaskDTO NULL_STATE = new DetailScreen.Presenter.TaskDTO(null, -1, "", "");


    public DetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) { // IDEA Editor
            return;
        }
        Mortar.inject(context, this);
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
        ButterKnife.reset(this);
        presenter.dropView(this);
    }

    /*
     *  Display logic
     */

    private void setEditedTitle(String title) {
        checkNotNull(title, "title must not be null");
        checkState(null != editTitle, "editTitle not injected");
        editTitle.setText(title);
    }

    private String getEditedTitle() {
        checkState(null != editTitle, "editTitle not injected");
        return editTitle.getText().toString();
    }

    private void setEditedDescription(String taskDescription) {
        checkNotNull(taskDescription, "taskDescription must not be null");
        checkState(null != editDescription, "editDescription not injected");
        editDescription.setText(taskDescription);
    }

    private String getEditedDescription() {
        checkState(null != editDescription, "editDescription not injected");
        return editDescription.getText().toString();
    }

    private void setTaskVersion(int version) {
        showVersion.setText(String.format("%d", version));
    }

    private void setTaskId(UUID id) {
        checkNotNull(id, "id must not be null");
        showUUID.setText(id.toString());
    }

    public void setEditState(@CheckForNull DetailScreen.Presenter.TaskDTO editState) {
        show(editState != null ? editState : NULL_STATE);
    }

    public  DetailScreen.Presenter.TaskDTO getEditState() {
        DetailScreen.Presenter.TaskDTO editState = originalState.withTitle(getEditedTitle()).withDescription(getEditedDescription());
        return editState;
    }

    /**
     *
     */
    public void setOriginalState(@CheckForNull DetailScreen.Presenter.TaskDTO originalState) {
        this.originalState = originalState;
        show(this.originalState != null ? this.originalState : NULL_STATE);

    }

    private void show(DetailScreen.Presenter.TaskDTO state) {
        checkNotNull(state, "state must not be null");

        setTaskId(state.id);
        setEditedTitle(state.title);
        setEditedDescription(state.description);
        setTaskVersion(state.version);
    }

}
