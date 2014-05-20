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
package name.neuhalfen.todosimple.android.view.label;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import butterknife.InjectView;
import name.neuhalfen.todosimple.android.R;

import java.util.*;

public class LabelListView extends LinearLayout {
    @InjectView(R.id.label_list_add_label)
    Button addLabelButton;

    @InjectView(R.id.label_list_assigned_labels)
    LinearLayout assignedLabelViews;

    private Map<UUID, LabelDTO> assignedLabels;

    public LabelListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) { // IDEA Editor
            // return;
        }

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.label_list, this, true);


        assignedLabels = new HashMap<UUID, LabelDTO>();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        addLabelButton = (Button) findViewById(R.id.label_list_add_label);
        assignedLabelViews = (LinearLayout) findViewById(R.id.label_list_assigned_labels);
        // ButterKnife.inject(this);

        if (isInEditMode()) { // IDEA Editor
            // Add some demo data in the IDE editor
            UUID id = UUID.randomUUID();
            addLabel(new LabelDTO(id, "Demo label!"));

            id = UUID.randomUUID();
            addLabel(new LabelDTO(id, "Important"));

            id = UUID.randomUUID();
            addLabel(new LabelDTO(id, "another one"));
            return;
        }
        addLabelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final UUID id = UUID.randomUUID();
                addLabel(new LabelDTO(id, id.toString()));

            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (isInEditMode()) { // IDEA Editor
            // return;
        }
        //ButterKnife.reset(this);
        addLabelButton = null;
        assignedLabelViews = null;
    }


    public Set<LabelDTO> getAssignedLabels() {
        return Collections.unmodifiableSet(new HashSet<LabelDTO>(assignedLabels.values()));
    }

    protected void addLabel(LabelDTO label) {
        if (assignedLabels.containsKey(label.id)) {
            return;
        }

        assignedLabels.put(label.id, label);

        addLabelView(label);
    }

    private void removeLabel(LabelDTO label) {
        if (!assignedLabels.containsKey(label.id)) {
            return;
        }
        assignedLabels.remove(label.id);
        removeLabelView(label);

    }

    private void removeLabelView(LabelDTO label) {
        final View view = assignedLabelViews.findViewWithTag(label);
        assignedLabelViews.removeView(view);
    }

    private void addLabelView(LabelDTO label) {

        LayoutInflater li = LayoutInflater.from(getContext());
        Button tv = (Button) li.inflate(R.layout.label_button_view, assignedLabelViews, false);
        tv.setText(label.name);
        tv.setTag(label);

        tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final LabelDTO label = (LabelDTO) v.getTag();
                removeLabel(label);
            }
        });
        assignedLabelViews.addView(tv);
    }


    private void repopulateLabelsView() {
        assignedLabelViews.removeAllViews();
        for (LabelDTO label : assignedLabels.values()) {
            addLabelView(label);
        }
    }

}
