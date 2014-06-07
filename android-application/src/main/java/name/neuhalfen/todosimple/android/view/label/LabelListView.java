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
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import mortar.Mortar;
import mortar.MortarScope;
import name.neuhalfen.todosimple.android.R;

import javax.inject.Inject;
import java.util.*;

public class LabelListView extends LinearLayout {

    public interface OnLabelClickedListener {
        public void onLabelClicked(LabelListView view, LabelDTO label);
    }


    @Inject
    LabelListControl.Presenter presenter;

    private List<Button> allLabelViews;

    private final static class LabelViewConfig {
        final int labelTextColor;
        final int bgColor;
        final int borderColor;
        final int borderWidth;

        final Drawable background;

        private LabelViewConfig(int labelTextColor, int bgColor, int borderColor, int borderWidth, Drawable background) {
            this.labelTextColor = labelTextColor;
            this.bgColor = bgColor;
            this.borderColor = borderColor;
            this.borderWidth = borderWidth;
            this.background = background;
        }
    }

    private LabelViewConfig labelViewConfig;
    int marginSmall;
    int marginMedium;

    public LabelListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (isInEditMode()) { // IDEA Editor
            return;
        }

        hookUpMortar(context);

        labelViewConfig = buildLabelViewConfig(context, attrs);
        marginSmall = getResources().getDimensionPixelSize(R.dimen.margin_small);
        marginMedium = getResources().getDimensionPixelSize(R.dimen.margin_medium);

        setOrientation(LinearLayout.VERTICAL);
    }

    private void hookUpMortar(Context context) {
        final MortarScope myScope = Mortar.getScope(context);
        final MortarScope newChildScope = myScope.requireChild(new LabelListControl());
        final Context newChildScopeContext = newChildScope.createContext(context);
        Mortar.inject(newChildScopeContext, this);
    }

    private LabelViewConfig buildLabelViewConfig(Context context, AttributeSet attrs) {

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LabelListView);
        int bgColor = a.getColor(R.styleable.LabelListView_labelBackgroundColor, Color.GRAY);
        int borderColor = a.getColor(R.styleable.LabelListView_labelBorderColor, Color.BLACK);
        int borderWidth = a.getDimensionPixelSize(R.styleable.LabelListView_labelBorderWidth, getResources().getDimensionPixelSize(R.dimen.label_border_width));
        int labelTextColor = a.getColor(R.styleable.LabelListView_labelTextColor, Color.WHITE);
        a.recycle();

        final GradientDrawable background = (GradientDrawable) getResources().getDrawable(R.drawable.rounded_corners);
        background.setStroke(borderWidth, borderColor);
        background.setColor(bgColor);

        final LabelViewConfig cfg = new LabelViewConfig(labelTextColor, bgColor, borderColor, borderWidth, background);

        return cfg;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (isInEditMode()) { // IDEA Editor
            return;
        }

        allLabelViews = new ArrayList<Button>();

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
        setOnLabelClickedListener(null);
    }

    public void setOnLabelClickedListener(OnLabelClickedListener listener) {
        if (null != presenter) {
            presenter.setOnLabelClickedListener(listener);
        }
    }


    public Set<LabelDTO> getAssignedLabels() {
        return presenter.getAssignedLabels();
    }

    public void assignLabel(LabelDTO label) {
        presenter.assignLabel(label);
    }

    public void unassignLabel(LabelDTO label) {
        presenter.unassignLabel(label);
    }

    void removeLabelView(LabelDTO label) {
        //final View view = assignedLabelViews.findViewWithTag(label);
        //assignedLabelViews.removeView(view);
        // FIXME: Hacksih at its worst
        Button tobeRemoved = null;
        for (Button b : allLabelViews) {
            if (b.getTag().equals(label)) {
                tobeRemoved = b;
            }
        }
        if (null != tobeRemoved) {
            allLabelViews.remove(tobeRemoved);
            buildLabelRows(allLabelViews, getContext());
        }
    }

    void addLabelView(LabelDTO label) {

        LayoutInflater li = LayoutInflater.from(getContext());
        Button tv = (Button) li.inflate(R.layout.label_button_view, this, false);
        tv.setText(label.name);
        tv.setTag(label);
        tv.setTextColor(labelViewConfig.labelTextColor);
        tv.setBackground(labelViewConfig.background);
        tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final LabelDTO label = (LabelDTO) v.getTag();
                onLabelClicked(label);
            }
        });
        allLabelViews.add(tv);

        Collections.sort(allLabelViews,

                new Comparator<Button>() {
                    @Override
                    public int compare(Button lhs, Button rhs) {
                        LabelDTO ldto = (LabelDTO) lhs.getTag();
                        LabelDTO rdto = (LabelDTO) rhs.getTag();
                        return ldto.name.compareTo(rdto.name);
                    }
                });


        buildLabelRows(allLabelViews, getContext());
        invalidate();
    }

    private void onLabelClicked(LabelDTO label) {
        if (presenter != null) {
            presenter.onLabelClicked(label);
        }
    }

    /**
     * Row for the label view.
     */
    private final static class Row {
        private final int maxLength;
        private int currentLength;
        public final LinearLayout layoutRow;

        public Row(int maxLength, LinearLayout layoutRow) {
            this.maxLength = maxLength;
            this.layoutRow = layoutRow;
            this.currentLength = 0;
        }

        public boolean isEmpty() {
            return currentLength == 0;
        }

        public boolean fits(int viewMeasuredWith) {
            return viewMeasuredWith + currentLength <= maxLength;
        }

        public void append(View view, int viewMeasuredWidth) {
            // new LinearLayout.LayoutParams(viewMeasuredWidth, LayoutParams.WRAP_CONTENT)
            //layoutRow.addView(view, childLayoutParams);
            layoutRow.addView(view);
            currentLength += viewMeasuredWidth;
        }

        public boolean isFull() {
            return currentLength >= maxLength;
        }

        public int measureLabelWidth(View label) {
            label.measure(0, 0);
            final int measuredWidth = label.getMeasuredWidth();
            return measuredWidth;
        }
    }

    private void buildLabelRows(List<Button> views, Context context) {
        removeAllLabelRowsAndViews(views);

        final int maxWidth = this.getMeasuredWidth() - marginMedium;

        final LayoutParams rowLayoutParams = (LayoutParams) getLayoutParams();

        Row currentRow = new Row(maxWidth, buildRowLayout(context, rowLayoutParams));

        for (View view : views) {
            final int viewMeasuredWidth = currentRow.measureLabelWidth(view);

            if (currentRow.isEmpty() || currentRow.fits(viewMeasuredWidth)) {
                currentRow.append(view, viewMeasuredWidth);
            } else {
                addView(currentRow.layoutRow);

                currentRow = new Row(maxWidth, buildRowLayout(context, rowLayoutParams));
                currentRow.append(view, viewMeasuredWidth);
            }
        }

        if (!currentRow.isEmpty()) {
            addView(currentRow.layoutRow);
        }
        invalidate();
    }

    private void removeAllLabelRowsAndViews(List<Button> views) {
        // FIXME HACK: remove all children from the parent
        for (View view : views) {
            if (view.getParent() != null) {
                if (view.getParent() instanceof LinearLayout) {
                    ((LinearLayout) view.getParent()).removeView(view);
                }
            }
        }
        // this alone does not help
        removeAllViews();
    }


    private LinearLayout buildRowLayout(Context context, LayoutParams layoutParams) {
        LinearLayout currentRow;
        currentRow = new LinearLayout(context);
        currentRow.setLayoutParams(layoutParams);
        currentRow.setGravity(Gravity.LEFT);
        currentRow.setOrientation(LinearLayout.HORIZONTAL);
        return currentRow;
    }


}
