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
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import mortar.Mortar;
import mortar.MortarScope;
import name.neuhalfen.todosimple.android.R;

import javax.inject.Inject;
import java.util.*;

public class LabelListView extends LinearLayout {
    @Inject
    LabelListControl.Presenter presenter;

    private SortedSet<Button> allLabelViews;

    int labelTextColor;

    public LabelListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (isInEditMode()) { // IDEA Editor
            final View view = LayoutInflater.from(context).inflate(R.layout.label_list, null);
            addView(view);
            return;
        }

        final MortarScope myScope = Mortar.getScope(context);
        final MortarScope newChildScope = myScope.requireChild(new LabelListControl());
        final Context newChildScopeContext = newChildScope.createContext(context);

        final View view = LayoutInflater.from(newChildScopeContext).inflate(R.layout.label_list, null);


        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LabelListView);
        int bgColor = a.getColor(R.styleable.LabelListView_backgroundColor, Color.GRAY);
        int borderColor = a.getColor(R.styleable.LabelListView_borderColor, Color.BLACK);
        int borderWidth = a.getDimensionPixelSize(R.styleable.LabelListView_borderWidth, getResources().getDimensionPixelSize(R.dimen.label_border_width));

        this.labelTextColor = a.getColor(R.styleable.LabelListView_textColor, Color.WHITE);

        a.recycle();

        final GradientDrawable background = (GradientDrawable) view.getBackground();
        background.setStroke(borderWidth, borderColor);
        background.setColor(bgColor);

        addView(view);

        Mortar.inject(newChildScopeContext, this);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (isInEditMode()) { // IDEA Editor
            return;
        }

        allLabelViews = new TreeSet<Button>(new Comparator<Button>() {
            @Override
            public int compare(Button lhs, Button rhs) {
                LabelDTO ldto = (LabelDTO) lhs.getTag();
                LabelDTO rdto = (LabelDTO) rhs.getTag();
                return ldto.name.compareTo(rdto.name);
            }
        });

        presenter.takeView(this);

        if (isInEditMode()) { // IDEA Editor
            // Add some demo data in the IDE editor
            UUID id = UUID.randomUUID();
            assignLabel(new LabelDTO(id, "Demo label!"));

            id = UUID.randomUUID();
            assignLabel(new LabelDTO(id, "Important"));

            id = UUID.randomUUID();
            assignLabel(new LabelDTO(id, "another one"));
            return;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (isInEditMode()) { // IDEA Editor
            return;
        }

        presenter.dropView(this);

        allLabelViews = null;
    }


    public Set<LabelDTO> getAssignedLabels() {
        return presenter.getAssignedLabels();
    }

    public void assignLabel(LabelDTO label) {
        presenter.assignLabel(label);
    }

    private void unassignLabel(LabelDTO label) {
        presenter.unassignLabel(label);
    }

    void removeLabelView(LabelDTO label) {
        //final View view = assignedLabelViews.findViewWithTag(label);
        //assignedLabelViews.removeView(view);
        // FIXME: Hacksih at its worst
        Button tobeRemoved = null;
        for (Button b : allLabelViews) {
            if (b.getTag() == label) {
                tobeRemoved = b;
            }
        }
        allLabelViews.remove(tobeRemoved);
        populateViews(this, allLabelViews, getContext());
    }

    void addLabelView(LabelDTO label) {

        LayoutInflater li = LayoutInflater.from(getContext());
        Button tv = (Button) li.inflate(R.layout.label_button_view, this, false);
        tv.setText(label.name);
        tv.setTag(label);
        tv.setTextColor(this.labelTextColor);

        tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final LabelDTO label = (LabelDTO) v.getTag();
                unassignLabel(label);
            }
        });
        allLabelViews.add(tv);

        populateViews(this, allLabelViews, getContext());
    }


    /**
     * Copyright 2011 Sherif
     * Updated by Karim Varela to handle LinearLayouts with other views on either side.
     *
     * @param linearLayout
     * @param views        : The views to wrap within LinearLayout
     * @param context
     * @author Karim Varela
     */
    private void populateViews(LinearLayout linearLayout, SortedSet<Button> views, Context context) {

        // FIXME HACK: remove all children from the parent
        for (View view : views) {
            if (view.getParent() != null) {
                if (view.getParent() instanceof LinearLayout) {
                    ((LinearLayout) view.getParent()).removeView(view);
                }
            }
        }
        // this alone does not help
        linearLayout.removeAllViews();

        int maxWidth = this.getMeasuredWidth() - getResources().getDimensionPixelSize(R.dimen.margin_small);

        linearLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params;
        LinearLayout newLL = new LinearLayout(context);
        newLL.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        newLL.setGravity(Gravity.LEFT);
        newLL.setOrientation(LinearLayout.HORIZONTAL);

        int widthSoFar = 0;

        for (View view : views) {
            LinearLayout LL = new LinearLayout(context);
            LL.setOrientation(LinearLayout.HORIZONTAL);
            LL.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
            LL.setLayoutParams(new ListView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

            view.measure(0, 0);
            params = new LinearLayout.LayoutParams(view.getMeasuredWidth(), LayoutParams.WRAP_CONTENT);
            //params.setMargins(5, 0, 5, 0);

            LL.addView(view, params);
            LL.measure(0, 0);
            widthSoFar += view.getMeasuredWidth();
            if (widthSoFar >= maxWidth) {
                linearLayout.addView(newLL);

                newLL = new LinearLayout(context);
                newLL.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                newLL.setOrientation(LinearLayout.HORIZONTAL);
                newLL.setGravity(Gravity.LEFT);
                params = new LinearLayout.LayoutParams(LL.getMeasuredWidth(), LL.getMeasuredHeight());
                newLL.addView(LL, params);
                widthSoFar = LL.getMeasuredWidth();
            } else {
                newLL.addView(LL);
            }
        }
        linearLayout.addView(newLL);
    }


}
