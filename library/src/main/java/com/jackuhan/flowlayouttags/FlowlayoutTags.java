package com.jackuhan.flowlayouttags;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ValueAnimator;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by hanjiahu on 15/9/5.
 */
public class FlowlayoutTags extends ViewGroup {
    private final int default_text_color = Color.rgb(0x49, 0xC1, 0x20);
    private final int default_background_color = Color.WHITE;
    private final int default_input_hint_color = Color.argb(0x80, 0x00, 0x00, 0x00);
    private final int default_input_text_color = Color.argb(0xDE, 0x00, 0x00, 0x00);
    private final int default_checked_text_color = Color.WHITE;
    private final int default_checked_background_color = Color.rgb(0x49, 0xC1, 0x20);
    private final int default_pressed_background_color = Color.rgb(0xED, 0xED, 0xED);
    private final float default_text_size;
    private final float default_horizontal_spacing;
    private final float default_vertical_spacing;
    private final float default_horizontal_padding;
    private final float default_vertical_padding;

    private int textColor;
    private int backgroundColor;
    private int inputHintColor;
    private int inputTextColor;
    private int checkedTextColor;
    private int checkedBackgroundColor;
    private int pressedBackgroundColor;
    private float textSize;
    private int horizontalSpacing;
    private int verticalSpacing;
    private int horizontalPadding;
    private int verticalPadding;
    private int tagWidth, tagHeight;
    private int tagMaxEms;
    private boolean multiChooseable, singleLine;

    private boolean animUpdateDrawable = false;

    //textview的属性
    private float mRadius[] = {0, 0, 0, 0, 0, 0, 0, 0};
    private ColorStateList mStrokeColor, mCheckedStrokeColor;
    private int mStrokeWidth = 0;


    private OnTagChangeListener mOnTagChangeListener;

    private OnTagClickListener mOnTagClickListener;

    private InternalTagClickListener mInternalTagClickListener = new InternalTagClickListener();

    public FlowlayoutTags(Context context) {
        this(context, null);
    }

    public FlowlayoutTags(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.flowlayoutTagsStyle);
    }

    public FlowlayoutTags(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        default_text_size = sp2px(13.0f);
        default_horizontal_spacing = dp2px(8.0f);
        default_vertical_spacing = dp2px(4.0f);
        default_horizontal_padding = dp2px(0.0f);
        default_vertical_padding = dp2px(0.0f);

        final TypedArray attrsArray = context.obtainStyledAttributes(attrs, R.styleable.FlowlayoutTags, defStyleAttr, R.style.FlowlayoutTags);
        try {
            textColor = attrsArray.getColor(R.styleable.FlowlayoutTags_tag_textColor, default_text_color);
            backgroundColor = attrsArray.getColor(R.styleable.FlowlayoutTags_tag_backgroundColor, default_background_color);
            inputHintColor = attrsArray.getColor(R.styleable.FlowlayoutTags_tag_inputHintColor, default_input_hint_color);
            inputTextColor = attrsArray.getColor(R.styleable.FlowlayoutTags_tag_inputTextColor, default_input_text_color);
            checkedTextColor = attrsArray.getColor(R.styleable.FlowlayoutTags_tag_checkedTextColor, default_checked_text_color);
            checkedBackgroundColor = attrsArray.getColor(R.styleable.FlowlayoutTags_tag_checkedBackgroundColor, default_checked_background_color);
            pressedBackgroundColor = attrsArray.getColor(R.styleable.FlowlayoutTags_tag_pressedBackgroundColor, default_pressed_background_color);
            textSize = attrsArray.getDimension(R.styleable.FlowlayoutTags_tag_textSize, default_text_size);
            horizontalSpacing = (int) attrsArray.getDimension(R.styleable.FlowlayoutTags_tag_horizontalSpacing, default_horizontal_spacing);
            verticalSpacing = (int) attrsArray.getDimension(R.styleable.FlowlayoutTags_tag_verticalSpacing, default_vertical_spacing);
            horizontalPadding = (int) attrsArray.getDimension(R.styleable.FlowlayoutTags_tag_horizontalPadding, default_horizontal_padding);
            verticalPadding = (int) attrsArray.getDimension(R.styleable.FlowlayoutTags_tag_verticalPadding, default_vertical_padding);
            multiChooseable = attrsArray.getBoolean(R.styleable.FlowlayoutTags_tag_multiChooseable, true);
            singleLine = attrsArray.getBoolean(R.styleable.FlowlayoutTags_tag_singleLine, false);
            tagWidth = attrsArray.getInt(R.styleable.FlowlayoutTags_tag_width, FlowlayoutTags.LayoutParams.WRAP_CONTENT);
            if (tagWidth >= 0) {
                tagWidth = sp2px(tagWidth);
            }
            tagHeight = attrsArray.getInt(R.styleable.FlowlayoutTags_tag_height, FlowlayoutTags.LayoutParams.WRAP_CONTENT);
            if (tagHeight >= 0) {
                tagHeight = sp2px(tagHeight);
            }

            tagMaxEms = attrsArray.getInt(R.styleable.FlowlayoutTags_tag_maxEms, -1);
            if (tagWidth < 0) {
                tagMaxEms = -1;
            }

            float radius = attrsArray.getDimension(R.styleable.FlowlayoutTags_tag_radius, 0);
            float topLeftRadius = attrsArray.getDimension(R.styleable.FlowlayoutTags_tag_topLeftRadius, 0);
            float topRightRadius = attrsArray.getDimension(R.styleable.FlowlayoutTags_tag_topRightRadius, 0);
            float bottomLeftRadius = attrsArray.getDimension(R.styleable.FlowlayoutTags_tag_bottomLeftRadius, 0);
            float bottomRightRadius = attrsArray.getDimension(R.styleable.FlowlayoutTags_tag_bottomRightRadius, 0);

            if (topLeftRadius == 0 && topRightRadius == 0 && bottomLeftRadius == 0
                    && bottomRightRadius == 0) {
                topLeftRadius = topRightRadius = bottomRightRadius = bottomLeftRadius = radius;
            }

            mRadius[0] = mRadius[1] = topLeftRadius;
            mRadius[2] = mRadius[3] = topRightRadius;
            mRadius[4] = mRadius[5] = bottomRightRadius;
            mRadius[6] = mRadius[7] = bottomLeftRadius;

            mStrokeColor = attrsArray.getColorStateList(R.styleable.FlowlayoutTags_tag_strokeColor);
            mCheckedStrokeColor = attrsArray.getColorStateList(R.styleable.FlowlayoutTags_tag_checkedStrokeColor);
            mStrokeWidth = (int) attrsArray.getDimension(R.styleable.FlowlayoutTags_tag_strokeWidth, mStrokeWidth);


        } finally {
            attrsArray.recycle();
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int width = 0;
        int height = 0;

        int row = 0; // The row counter.
        int rowWidth = 0; // Calc the current row width.
        int rowMaxHeight = 0; // Calc the max tag height, in current row.

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final int childWidth = child.getMeasuredWidth();
            final int childHeight = child.getMeasuredHeight();

            if (child.getVisibility() != GONE) {
                rowWidth += childWidth;
                if (rowWidth > widthSize) { // Next line.
                    rowWidth = childWidth; // The next row width.
                    height += rowMaxHeight + verticalSpacing;
                    rowMaxHeight = childHeight; // The next row max height.
                    row++;
                } else { // This line.
                    rowMaxHeight = Math.max(rowMaxHeight, childHeight);
                }
                rowWidth += horizontalSpacing;
            }
        }
        // Account for the last row height.
        height += rowMaxHeight;

        // Account for the padding too.
        height += getPaddingTop() + getPaddingBottom();

        // If the tags grouped in one row, set the width to wrap the tags.
        if (row == 0) {
            width = rowWidth;
            width += getPaddingLeft() + getPaddingRight();
        } else {// If the tags grouped exceed one line, set the width to match the parent.
            width = widthSize;
        }

        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : width,
                heightMode == MeasureSpec.EXACTLY ? heightSize : height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int parentLeft = getPaddingLeft();
        final int parentRight = r - l - getPaddingRight();
        final int parentTop = getPaddingTop();
        final int parentBottom = b - t - getPaddingBottom();

        int childLeft = parentLeft;
        int childTop = parentTop;

        int rowMaxHeight = 0;

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final int width = child.getMeasuredWidth();
            final int height = child.getMeasuredHeight();

            if (child.getVisibility() != GONE) {
                if (childLeft + width > parentRight) { // Next line
                    childLeft = parentLeft;
                    childTop += rowMaxHeight + verticalSpacing;
                    rowMaxHeight = height;
                } else {
                    rowMaxHeight = Math.max(rowMaxHeight, height);
                }
                child.layout(childLeft, childTop, childLeft + width, childTop + height);

                childLeft += width + horizontalSpacing;
            }
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.tags = getTags();
        ss.checkedPosition = getCheckedTagIndex();
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        setTags(ss.tags);
        TagView checkedTagView = getTagAt(ss.checkedPosition);
        if (checkedTagView != null) {
            checkedTagView.setCheckedWithoutAnimal(true);
        }
    }

    /**
     * Return the last NORMAL state tag view in this group.
     *
     * @return the last NORMAL state tag view or null if not exists
     */
    protected TagView getLastTagView() {
        final int lastNormalTagIndex = getChildCount() - 1;
        return getTagAt(lastNormalTagIndex);
    }

    /**
     * Returns the tag array in group, except the INPUT tag.
     *
     * @return the tag array.
     */
    public String[] getTags() {
        final int count = getChildCount();
        final List<String> tagList = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            final TagView tagView = getTagAt(i);
            tagList.add(tagView.getText().toString());
        }

        return tagList.toArray(new String[tagList.size()]);
    }

    /**
     * @see #setTags(String...)
     */
    public void setTags(List<String> tagList) {
        setTags(tagList.toArray(new String[tagList.size()]));
    }

    /**
     * Set the tags. It will remove all previous tags first.
     *
     * @param tags the tag list to set.
     */
    public void setTags(String... tags) {
        removeAllViews();
        for (final String tag : tags) {
            appendTag(tag);
        }

    }

    /**
     * Returns the tag view at the specified position in the group.
     *
     * @param index the position at which to get the tag view from.
     * @return the tag view at the specified position or null if the position
     * does not exists within this group.
     */
    protected TagView getTagAt(int index) {
        return null == getChildAt(index) ? null : (TagView) getChildAt(index);
    }

    /**
     * Returns the checked tag view in the group.单选时候有用,多选时候返回第一个
     *
     * @return the checked tag view or null if not exists.
     */
    protected TagView getCheckedTag() {
        final int checkedTagIndex = getCheckedTagIndex();
        if (checkedTagIndex != -1) {
            return getTagAt(checkedTagIndex);
        }
        return null;
    }

    /**
     * Returns the checked tag view in the group.单选时候有用,多选时候返回第一个
     *
     * @return the checked tag view or null if not exists.
     */
    protected String getCheckedTagText() {
        if (null != getCheckedTag()) {
            return getCheckedTag().getText().toString();
        }
        return null;
    }

    /**
     * 返回选中的tag的文字
     * Returns the tag array in group, except the INPUT tag.
     *
     * @return the tag array.
     */
    public String[] getCheckedTagsText() {
        final int count = getChildCount();
        final List<String> tagList = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            final TagView tagView = getTagAt(i);
            if (tagView.isChecked) {
                tagList.add(tagView.getText().toString());
            }
        }

        return tagList.toArray(new String[tagList.size()]);
    }

    /**
     * 返回选中的tag的文字
     * Returns the tag array in group, except the INPUT tag.
     *
     * @return the tag array.
     */
    public ArrayList<String> getCheckedTagsTextsArrayList() {
        final int count = getChildCount();
        final ArrayList<String> tagList = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            final TagView tagView = getTagAt(i);
            if (tagView.isChecked) {
                tagList.add(tagView.getText().toString());
            }
        }

        return tagList;
    }

    /**
     * 单选时候有用,多选时候返回第一个
     * Return the checked tag index.
     *
     * @return the checked tag index, or -1 if not exists.
     */
    protected int getCheckedTagIndex() {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final TagView tag = getTagAt(i);
            if (tag.isChecked) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 返回选中的tag的索引
     * Returns the tag array in group, except the INPUT tag.
     *
     * @return the tag array.
     */
    public ArrayList<Integer> getCheckedTagsIndexArrayList() {
        final int count = getChildCount();
        final ArrayList<Integer> tagList = new ArrayList<Integer>();
        for (int i = 0; i < count; i++) {
            final TagView tagView = getTagAt(i);
            if (tagView.isChecked) {
                tagList.add(i);
            }
        }

        return tagList;
    }

    /**
     * 取消选中状态
     *
     * @param animal 是否有颜色过渡动画
     */
    public void setTagsUncheckedColorAnimal(boolean animal) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            TagView tag = getTagAt(i);
            if (null != tag && tag.isChecked) {
                if (animal) {
                    tag.setCheckedColorAnimal(false);
                } else {
                    tag.setCheckedWithoutAnimal(false);
                }
            }
        }
    }

    /**
     * Register a callback to be invoked when this tag group is changed.
     *
     * @param l the callback that will run
     */
    public void setOnTagChangeListener(OnTagChangeListener l) {
        mOnTagChangeListener = l;
    }


    /**
     * Append tag to this group.
     *
     * @param tag the tag to append.
     */
    public void appendTag(CharSequence tag) {
        final TagView newTag = new TagView(getContext(), tag);
//        if (singleLine) {
//            newTag.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
//            newTag.setMaxEms(2);
//        }
//        newTag.setSingleLine(singleLine);
        newTag.setOnClickListener(mInternalTagClickListener);
        addView(newTag);
    }

    public float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    public int sp2px(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                getResources().getDisplayMetrics());
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new FlowlayoutTags.LayoutParams(getContext(), attrs);
    }

    /**
     * Register a callback to be invoked when a tag is clicked.
     *
     * @param l the callback that will run.
     */
    public void setOnTagClickListener(OnTagClickListener l) {
        mOnTagClickListener = l;
    }


    /**
     * Interface definition for a callback to be invoked when a tag group is changed.
     */
    public interface OnTagChangeListener {
        /**
         * Called when a tag has been appended to the group.
         *
         * @param tag the appended tag.
         */
        void onAppend(FlowlayoutTags flowlayoutTags, String tag);

        /**
         * Called when a tag has been deleted from the the group.
         *
         * @param tag the deleted tag.
         */
        void onDelete(FlowlayoutTags flowlayoutTags, String tag);
    }

    /**
     * Interface definition for a callback to be invoked when a tag is clicked.
     */
    public interface OnTagClickListener {
        /**
         * Called when a tag has been clicked.
         *
         * @param tag The tag text of the tag that was clicked.
         */
        void onTagClick(String tag);
    }

    /**
     * Per-child layout information for layouts.
     */
    public static class LayoutParams extends ViewGroup.LayoutParams {
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }
    }

    /**
     * For {@link FlowlayoutTags} save and restore state.
     */
    static class SavedState extends BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
        int tagCount;
        String[] tags;
        int checkedPosition;

        public SavedState(Parcel source) {
            super(source);
            tagCount = source.readInt();
            tags = new String[tagCount];
            source.readStringArray(tags);
            checkedPosition = source.readInt();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            tagCount = tags.length;
            dest.writeInt(tagCount);
            dest.writeStringArray(tags);
            dest.writeInt(checkedPosition);
        }
    }

    /**
     * The tag view click listener for internal use.
     */
    class InternalTagClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            final TagView tag = (TagView) v;

            final TagView checkedTag = getCheckedTag();
            if (!multiChooseable) {
                //单选
                if (checkedTag != null) {
                    checkedTag.setCheckedWithoutAnimal(false);
                }

                tag.setCheckedWithoutAnimal(true);
            } else {
                //多选
                tag.setCheckedWithoutAnimal(!tag.isChecked);
            }

            //外部点击事件
            if (mOnTagClickListener != null) {
                mOnTagClickListener.onTagClick(tag.getText().toString());
            }

        }
    }

    /**
     * The tag view which has two states can be either NORMAL or INPUT.
     */
    class TagView extends TextView {

        private Context mContext;
        private boolean isChecked = false;
        private boolean isPressed = false;

        private Paint mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        /**
         * Used to detect the touch event.
         */
        private Rect mOutRect = new Rect();

        {
            mBackgroundPaint.setStyle(Paint.Style.FILL);
        }


        public TagView(Context context, CharSequence text) {
            super(context);
            this.mContext = context;
            setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
            Log.v("Hanjh", "tagWidth " + tagWidth + " tagHeight " + tagHeight);
            setLayoutParams(new FlowlayoutTags.LayoutParams(tagWidth, tagHeight));

            setGravity(Gravity.CENTER);

            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

            setSingleLine(singleLine);
            if (singleLine) {
                if (tagMaxEms >= 0) {
                    setEllipsize(TextUtils.TruncateAt.valueOf("END"));
                    setMaxEms(tagMaxEms);
                }
//                setWidth(5);
            }

            setText(text);

            setClickable(true);
            invalidatePaint();

        }


        /**
         * Set whether this tag view is in the checked state.
         *
         * @param checked true is checked, false otherwise
         */
        public void setCheckedWithoutAnimal(boolean checked) {
            isChecked = checked;

            invalidatePaint();
        }

        /**
         * Set whether this tag view is in the checked state.
         *
         * @param checked true is checked, false otherwise
         */
        public void setCheckedColorAnimal(boolean checked) {
            isChecked = checked;
            changeColorTransition(checked);

        }

        private void changeColorTransition(final boolean checked) {
            animUpdateDrawable = true;

            int fromColor, toColor;
            if (checked) {
                fromColor = backgroundColor;
                toColor = checkedBackgroundColor;
            } else {
                fromColor = checkedBackgroundColor;
                toColor = backgroundColor;
            }

            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), fromColor, toColor);
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {

                    GradientDrawable toDrawable = new GradientDrawable();
                    toDrawable.setCornerRadii(mRadius);
                    toDrawable.setColor((Integer) animator.getAnimatedValue());
                    toDrawable.setStroke(mStrokeWidth, !checked ? mStrokeColor.getDefaultColor() : mCheckedStrokeColor.getDefaultColor());

                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        setBackgroundDrawable(toDrawable);
                    } else {
                        setBackground(toDrawable);
                    }

                }

            });

            colorAnimation.addListener(new ValueAnimator.AnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    animUpdateDrawable = false;

                    if (checked) {
                        setTextColor(checkedTextColor);
                    } else {
                        setTextColor(textColor);
                    }
//                    invalidatePaint();
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }

                @Override
                public void onAnimationStart(Animator animator) {
                }
            });
            colorAnimation.setDuration(350);
            colorAnimation.start();
        }

        @Override
        protected boolean getDefaultEditable() {
            return true;
        }

        /**
         * Indicates whether the input content is available.
         *
         * @return True if the input content is available, false otherwise.
         */
        public boolean isInputAvailable() {
            return getText() != null && getText().length() > 0;
        }

        private void invalidatePaint() {

            animUpdateDrawable = false;

            if (isChecked) {
                mBackgroundPaint.setColor(checkedBackgroundColor);
                setTextColor(checkedTextColor);
            } else {
                mBackgroundPaint.setColor(backgroundColor);
                setTextColor(textColor);
            }

            if (isPressed) {
                mBackgroundPaint.setColor(pressedBackgroundColor);
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (!animUpdateDrawable) {
                updateDrawable();
            }
            super.onDraw(canvas);
        }

        private void updateDrawable() {
            mStrokeColor = mStrokeColor == null ? ColorStateList.valueOf(Color.TRANSPARENT) : mStrokeColor;
            mCheckedStrokeColor = mCheckedStrokeColor == null ? mStrokeColor : mCheckedStrokeColor;
            updateDrawable(!isChecked ? mStrokeColor.getDefaultColor() : mCheckedStrokeColor.getDefaultColor());
        }

        private void updateDrawable(int strokeColor) {

            int mbackgroundColor;
            if (isChecked) {
                mbackgroundColor = checkedBackgroundColor;
            } else {
                mbackgroundColor = backgroundColor;
            }

            if (isPressed) {
                mbackgroundColor = pressedBackgroundColor;
            }
            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadii(mRadius);
            drawable.setColor(mbackgroundColor);
            drawable.setStroke(mStrokeWidth, strokeColor);

            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                this.setBackgroundDrawable(drawable);
            } else {
                this.setBackground(drawable);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    getDrawingRect(mOutRect);
                    isPressed = true;
                    invalidatePaint();
                    invalidate();
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    if (!mOutRect.contains((int) event.getX(), (int) event.getY())) {
                        isPressed = false;
                        invalidatePaint();
                        invalidate();
                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    isPressed = false;
                    invalidatePaint();
                    invalidate();
                    break;
                }
            }
            return super.onTouchEvent(event);
        }

        @Override
        public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
            return new ZanyInputConnection(super.onCreateInputConnection(outAttrs), true);
        }

        /**
         * Solve edit text delete(backspace) key detect, see<a href="http://stackoverflow.com/a/14561345/3790554">
         * Android: Backspace in WebView/BaseInputConnection</a>
         */
        private class ZanyInputConnection extends InputConnectionWrapper {
            public ZanyInputConnection(android.view.inputmethod.InputConnection target, boolean mutable) {
                super(target, mutable);
            }

            @Override
            public boolean deleteSurroundingText(int beforeLength, int afterLength) {
                // magic: in latest Android, deleteSurroundingText(1, 0) will be called for backspace
                if (beforeLength == 1 && afterLength == 0) {
                    // backspace
                    return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                            && sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
                }
                return super.deleteSurroundingText(beforeLength, afterLength);
            }
        }
    }
}