package com.slidinguppanel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;

public class SlidingUpPanelLayout extends ViewGroup {
    /**
     * Float for expanded state
     */
    public static final float EXPANDED = 0.f;
    /**
     * Float for collapsed state
     */
    public static final float COLLAPSED = 1.f;
    /**
     * Default peeking out dragger height
     */
    private static final int DEFAULT_DRAGGER_HEIGHT = 40; // px;
    /**
     * Default height of the shadow above the peeking out dragger
     */
    private static final int DEFAULT_SHADOW_HEIGHT = 4; // dp;
    /**
     * If no fade color is given by default it will fade to 80% gray.
     */
    private static final int DEFAULT_FADE_COLOR = Color.BLACK;
    /**
     * Minimum velocity that will be detected as a fling
     */
    private static final int MIN_FLING_VELOCITY = 400; // dips per second
    /**
     * The default content height. Integer.MAX_VALUE = not set.
     */
    private static final int DEFAULT_CONTENT_HEIGHT = Integer.MAX_VALUE;
    /**
     * The default config for allowing tap event on dragger.
     */
    private static final boolean DEFAULT_ALLOW_TAPEVENT = true;
    /**
     * The default max height ratio is set to 2/3rd of the screen height
     */
    private static final float DEFAULT_MAX_HEIGHT_RATIO = 2.0f / 3;
    /**
     * The default for allowing action up events
     */
    private static final boolean DEFAULT_ACTION_UP_ALLOWED = false;
    /**
     * The paint used to dim the main layout when sliding
     */
    private final Paint mCoveredFadePaint = new Paint();
    /**
     * The size of the shadow in pixels.
     */
    private int mShadowHeight;
    /**
     * Helper class for making the sliding possible, based on support-v4-r13.jar lib.
     */
    private final ViewDragHelper mDragHelper;
    private final Rect mTmpRect = new Rect();
    /**
     * The fade color used for the slider. 0 = no fading.
     */
    private int mCoveredFadeColor = DEFAULT_FADE_COLOR;
    /**
     * Drawable used to draw the shadow.
     */
    private Drawable mShadowDrawable;
    /**
     * The size of the overhang in pixels.
     */
    private int mDraggerHeight;
    /**
     * True if the slidinguppanel can slide with the current measurements
     */
    private boolean mCanSlide;
    /**
     * The config for allowing tap event on dragger or not.
     */
    private boolean mInterceptPanelEvents;
    /**
     * If provided, the dragger can be dragged by only this view. Otherwise, the entire slidinguppanel can be
     * used for dragging.
     */
    private View mDraggerView;
    /**
     * The child view that can slide, if any.
     */
    private View mSlidableView;
    /**
     * How far the dragger is offset from its expanded position.
     * range [0, 1] where 0 = expanded, 1 = collapsed.
     */
    private float mSlideOffset;
    /**
     *
     */
    private boolean mActionUpEnabled;
    /**
     * How far in pixels the slideable dragger may move.
     */
    private int mSlideRange;
    /**
     * A view is locked into internal scrolling or another condition that
     * is preventing a drag.
     */
    private boolean mIsUnableToDrag;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private boolean mDragViewHit;
    /**
     * Sliding listener, for doing an action after expanded/collapsed/sliding state.
     */
    private PanelSlideListener mPanelSlideListener;
    /**
     * Set the max height value of the content view.
     */
    private float mMaxContentHeightRatio;
    /**
     * Current height of the slidinguppanel content, default set to the maximum integer value.
     */
    private int mContentHeight;
    /**
     * Stores whether or not the slidinguppanel was expanded the last time it was slideable.
     * If expand/collapse operations are invoked this state is modified. Used by
     * instance state save/restore.
     */
    private boolean mPreservedExpandedState;
    private boolean mFirstLayout = true;

    public SlidingUpPanelLayout(Context context) {
        this(context, null);
    }

    public SlidingUpPanelLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingUpPanelLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        final float density = context.getResources().getDisplayMetrics().density;

        setDraggerHeight(DEFAULT_DRAGGER_HEIGHT);
        mShadowHeight = DEFAULT_SHADOW_HEIGHT;
        mContentHeight = DEFAULT_CONTENT_HEIGHT;
        mInterceptPanelEvents = DEFAULT_ALLOW_TAPEVENT;
        mActionUpEnabled = DEFAULT_ACTION_UP_ALLOWED;

        setWillNotDraw(false);

        mDragHelper = ViewDragHelper.create(this, 0.5f, new DragHelperCallback());
        mDragHelper.setMinVelocity(MIN_FLING_VELOCITY * density);

        mCanSlide = true;

        setCoveredFadeColor(DEFAULT_FADE_COLOR);
    }

    /**
     * Check if the background of the given view is Opaque.
     *
     * @param view A given view.
     * @return true or false, opaque or not.
     */
    private static boolean hasOpaqueBackground(View view) {
        return view.getBackground() != null && view.getBackground().getOpacity() == PixelFormat.OPAQUE;
    }

    /**
     * Set the color used to fade.
     *
     * @param color An ARGB-packed color value
     */
    public void setCoveredFadeColor(int color) {
        mCoveredFadeColor = color;
        invalidate();
    }

    /**
     * Get the draggerview height in pixels
     *
     * @return mDraggerHeight the height of the dragger
     */
    public int getDraggerHeight() {
        return mDraggerHeight;
    }

    /**
     * Set the draggerview height in pixels
     *
     * @param height A height in pixels
     */
    public void setDraggerHeight(int height) {
        mDraggerHeight = height;
        invalidate();
    }

    /**
     * Action up event enabled or not
     *
     * @return mActionUpEnabled Boolean allow or not
     */
    public boolean isActionUpEnabled() {
        return mActionUpEnabled;
    }

    /**
     * Set the actionUpEnabled boolean value
     *
     * @param actionUpEnabled A boolean to allow / disallow action up events
     */
    public void setActionUpEnabled(boolean actionUpEnabled) {
        this.mActionUpEnabled = actionUpEnabled;
    }

    /**
     * Set a new, different from the default, PanelSlideListener
     *
     * @param panelSlideListener set the a custom panelSlideListener
     */
    public void setPanelSlideListener(PanelSlideListener panelSlideListener) {
        mPanelSlideListener = panelSlideListener;
    }

    /**
     * Calculate the distance from the top. On firstLayout take the paddingTop value.
     *
     * @param heightSize measured size of the view.
     * @return calculated paddingTop value.
     */
    public int getTopDistance(int heightSize) {
        int viewHeight = getMeasuredHeight() == 0 ? heightSize : getMeasuredHeight();
        int maxContentHeight = Math.round(viewHeight * getMaxContentHeightRatio() - getDraggerHeight());
        int contentHeight = Math.min(mContentHeight == DEFAULT_CONTENT_HEIGHT ? viewHeight : mContentHeight, maxContentHeight);
        int topValue = getPaddingTop();

        if (!mFirstLayout) {
            topValue = viewHeight - getDraggerHeight() - contentHeight;
        }

        return topValue;
    }

    /**
     * Return the max dragger + content ratio, if not set return 2/3 of the screensize ratio.
     *
     * @return percentage
     */
    public float getMaxContentHeightRatio() {
        return mMaxContentHeightRatio <= 0 ? DEFAULT_MAX_HEIGHT_RATIO : mMaxContentHeightRatio;
    }

    /**
     * Set the max dragger + content ratio
     *
     * @param ratio A ratio that will be used to calculate the max height of the entire view
     */
    public void setMaxContentHeightRatio(float ratio) {
        this.mMaxContentHeightRatio = ratio;
    }

    /**
     * Get the height of the content
     *
     * @return mContentHeight
     */
    public int getContentHeight() {
        return mContentHeight;
    }

    /**
     * Manually set the height of the content
     *
     * @param contentHeight The height of the content
     */
    public void setContentHeight(int contentHeight) {
        this.mContentHeight = contentHeight;
    }

    /**
     * Allow the user to reset the content to the default
     */
    public void resetContentHeight() {
        this.mContentHeight = DEFAULT_CONTENT_HEIGHT;
    }

    /**
     * Is the dragger view clickable or not.
     *
     * @return boolean to define clickable or not.
     */
    public boolean isInterceptingPanelEvents() {
        return mInterceptPanelEvents;
    }

    /**
     * Set if the dragger view may allow click events on the dragger view
     *
     * @param interceptTap true if clickable, false otherwise.
     */
    public void setInterceptingPanelEvents(boolean interceptTap) {
        this.mInterceptPanelEvents = interceptTap;
    }

    /**
     * Set the draggable view portion. Use to null, to allow the whole slidinguppanel to be draggable
     *
     * @param draggerView A view that will be used to drag the view.
     */
    public void setDraggerView(View draggerView, int height) {
        mDraggerView = draggerView;
        setDraggerHeight(height);
    }

    /**
     * Get the draggable view. Return the panel if set, else return the whole slidable view.
     *
     * @return panel view
     */
    public View getDraggerView() {
        return mDraggerView != null ? mDraggerView : mSlidableView;
    }

    /**
     * Set the shadow for the sliding dragger
     */
    public void setShadowDrawable(Drawable drawable) {
        mShadowDrawable = drawable;
    }

    /**
     * Set the top drawable for the sliding dragger with a new height
     */
    public void setShadowDrawable(Drawable drawable, int height) {
        mShadowDrawable = drawable;
        mShadowHeight = height;
    }

    /**
     * Define visibility on first layout.
     */
    private void updateObscuredViewVisibility() {
        if (getChildCount() == 0) {
            return;
        }
        final int leftBound = getPaddingLeft();
        final int rightBound = getWidth() - getPaddingRight();
        final int topBound = getTopDistance(0);
        final int bottomBound = getHeight() - getPaddingBottom();
        final int left;
        final int right;
        final int top;
        final int bottom;
        if (mSlidableView != null && hasOpaqueBackground(mSlidableView)) {
            left = mSlidableView.getLeft();
            right = mSlidableView.getRight();
            top = mSlidableView.getTop();
            bottom = mSlidableView.getBottom();
        } else {
            left = right = top = bottom = 0;
        }

        View child = getChildAt(0);
        final int clampedChildLeft = Math.max(leftBound, child.getLeft());
        final int clampedChildTop = Math.max(topBound, child.getTop());
        final int clampedChildRight = Math.min(rightBound, child.getRight());
        final int clampedChildBottom = Math.min(bottomBound, child.getBottom());
        final int visibility;
        if (clampedChildLeft >= left && clampedChildTop >= top &&
                clampedChildRight <= right && clampedChildBottom <= bottom) {
            visibility = INVISIBLE;
        } else {
            visibility = VISIBLE;
        }

        child.setVisibility(visibility);
    }

    /**
     * Loop over all children of the view and make them visible.
     */
    private void setAllChildrenVisible() {
        for (int i = 0, childCount = getChildCount(); i < childCount; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == INVISIBLE) {
                child.setVisibility(VISIBLE);
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mFirstLayout = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mFirstLayout = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        final int childCount = getChildCount();

        int layoutHeight = heightSize - getTopDistance(heightSize) - getPaddingBottom();
        int draggerHeight = mDraggerHeight;

        if (childCount > 2) {
            throw new RuntimeException("More than two child views are not supported");
        } else if (getChildAt(1).getVisibility() == GONE) {
            draggerHeight = 0;
        }

        // We'll find the current one below.
        mSlidableView = null;
        mCanSlide = false;

        // First pass. Measure based on child LayoutParams width/height.
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            int height = layoutHeight;

            if (i == 1) {
                lp.slideable = true;
                mSlidableView = child;
                mCanSlide = true;
            } else {
                height -= draggerHeight;
            }

            int childWidthSpec;
            if (lp.width == LayoutParams.WRAP_CONTENT) {
                childWidthSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.AT_MOST);
            } else if (lp.width == LayoutParams.MATCH_PARENT) {
                childWidthSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
            } else {
                childWidthSpec = MeasureSpec.makeMeasureSpec(lp.width, MeasureSpec.EXACTLY);
            }

            int childHeightSpec;
            if (lp.height == LayoutParams.WRAP_CONTENT) {
                childHeightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST);
            } else if (lp.height == LayoutParams.MATCH_PARENT) {
                childHeightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            } else {
                childHeightSpec = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
            }

            child.measure(childWidthSpec, childHeightSpec);
        }

        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getTopDistance(0);

        final int childCount = getChildCount();
        int yStart = paddingTop;
        int nextYStart = yStart;

        if (mFirstLayout) {
            mSlideOffset = mCanSlide && mPreservedExpandedState ? EXPANDED : COLLAPSED;
        }

        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE) {
                continue;
            }

            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int childHeight = child.getMeasuredHeight();

            if (lp.slideable) {
                mSlideRange = childHeight - mDraggerHeight;
                yStart += (int) (mSlideRange * mSlideOffset);
            } else {
                yStart = nextYStart;
            }

            final int childTop = (i == 0) ? getPaddingTop() : yStart;
            final int childBottom = childTop + childHeight;
            final int childRight = paddingLeft + child.getMeasuredWidth();
            child.layout(paddingLeft, childTop, childRight, childBottom);

            nextYStart += child.getHeight();
        }

        mFirstLayout = false;
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        // Recalculate views and their details
        if (height != oldHeight) {
            mFirstLayout = true;
        }
    }

    private boolean isDragViewHit(int x, int y) {
        View v = getDraggerView();
        if (v == null) {
            return false;
        }

        int[] viewLocation = new int[2];
        v.getLocationOnScreen(viewLocation);

        int[] parentLocation = new int[2];
        this.getLocationOnScreen(parentLocation);

        int screenX = parentLocation[0] + x;
        int screenY = parentLocation[1] + y;
        return screenX >= viewLocation[0] && screenX < viewLocation[0] + v.getWidth() &&
                screenY >= viewLocation[1] && screenY < viewLocation[1] + v.getHeight();
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        if (!isInTouchMode() && !mCanSlide) {
            mPreservedExpandedState = child == mSlidableView;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        final int action = MotionEventCompat.getActionMasked(motionEvent);

        if (!mCanSlide || (mIsUnableToDrag && action != MotionEvent.ACTION_DOWN)) {
            mDragHelper.cancel();
            return super.onInterceptTouchEvent(motionEvent);
        }

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mDragHelper.cancel();
            return false;
        }

        final float x = motionEvent.getX();
        final float y = motionEvent.getY();
        boolean interceptTap = false;

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mIsUnableToDrag = false;
                mInitialMotionX = x;
                mInitialMotionY = y;
                mDragViewHit = isDragViewHit((int) x, (int) y);

                if (mDragViewHit && !mInterceptPanelEvents) {
                    interceptTap = true;
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final float adx = Math.abs(x - mInitialMotionX);
                final float ady = Math.abs(y - mInitialMotionY);
                final int slop = mDragHelper.getTouchSlop();
                if (ady > slop && adx > ady) {
                    mDragHelper.cancel();
                    mIsUnableToDrag = true;
                    return false;
                }
            }
        }

        final boolean interceptForDrag = mDragViewHit && mDragHelper.shouldInterceptTouchEvent(motionEvent);
        return interceptForDrag || interceptTap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!mCanSlide) {
            return super.onTouchEvent(motionEvent);
        }

        mDragHelper.processTouchEvent(motionEvent);

        final float x = motionEvent.getX();
        final float y = motionEvent.getY();
        final boolean isDragViewHit = isDragViewHit((int) x, (int) y);

        switch (motionEvent.getAction() & MotionEventCompat.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                mInitialMotionX = x;
                mInitialMotionY = y;
            }
            case MotionEvent.ACTION_UP: {
                if (isActionUpEnabled()) {
                    final float dx = x - mInitialMotionX;
                    final float dy = y - mInitialMotionY;
                    final int slop = mDragHelper.getTouchSlop();
                    if (dx * dx + dy * dy < slop * slop && isDragViewHit) {
                        if (!isExpanded()) {
                            expand();
                        } else {
                            collapse();
                        }
                    }
                }
            }
        }

        return isDragViewHit || isExpanded();
    }

    /**
     * Expand the slidinguppanel if it is currently slideable. If first layout
     * has already completed this will animate.
     *
     * @return true if the slidinguppanel was slideable and is now expanded/in the process of expanding
     */
    public boolean expand() {
        if (mFirstLayout || smoothSlideTo(EXPANDED)) {
            mPreservedExpandedState = true;
            return true;
        }
        return false;
    }

    /**
     * Collapse the slidinguppanel if it is currently slideable. If first layout
     * has already completed this will animate.
     *
     * @return true if the slidinguppanel was slideable and is now collapsed/in the process of collapsing
     */
    public boolean collapse() {
        if (mFirstLayout || smoothSlideTo(COLLAPSED)) {
            mPreservedExpandedState = false;
            return true;
        }
        return false;
    }

    /**
     * Check if the layout is completely expanded.
     *
     * @return true if the slidinguppanel is completely expanded
     */
    public boolean isExpanded() {
        return mCanSlide && mSlideOffset == EXPANDED;
    }

    /**
     * Check if the content in this layout cannot fully fit side by side and therefore
     * the content can be slid back and forth.
     *
     * @return true if content in this layout can be expanded
     */
    public boolean isSlideable() {
        return mCanSlide;
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        final int save = canvas.save(Canvas.CLIP_SAVE_FLAG);

        boolean drawScrim = false;

        if (mCanSlide && !lp.slideable && mSlidableView != null) {
            // Clip against the slider; no sense drawing what will immediately be covered.
            canvas.getClipBounds(mTmpRect);
            mTmpRect.bottom = Math.min(mTmpRect.bottom, mSlidableView.getTop()) + ((mDraggerView != null) ? mDraggerView.getPaddingTop() : 0);
            canvas.clipRect(mTmpRect);

            drawScrim = true;
        }

        boolean result = super.drawChild(canvas, child, drawingTime);
        canvas.restoreToCount(save);

        if (drawScrim) {
            mCoveredFadePaint.setColor(mCoveredFadeColor);
            // alpha is from 0 to 255.
            final float ratio = (float) (getMeasuredHeight() - getDraggerHeight()) / 255;
            mCoveredFadePaint.setAlpha((int) (255 - mSlidableView.getTop() / ratio));
            canvas.drawRect(mTmpRect, mCoveredFadePaint);
        }

        return result;
    }

    /**
     * Smoothly animate to the target X position within its range.
     *
     * @param slideOffset position to animate to
     */
    private boolean smoothSlideTo(float slideOffset) {
        if (!mCanSlide) {
            // Nothing to do.
            return false;
        }

        int y = (int) (getTopDistance(0) + slideOffset * mSlideRange);

        if (mDragHelper.smoothSlideViewTo(mSlidableView, mSlidableView.getLeft(), y)) {
            setAllChildrenVisible();
            ViewCompat.postInvalidateOnAnimation(this);
            return true;
        }
        return false;
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            if (!mCanSlide) {
                mDragHelper.abort();
                return;
            }

            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        // No need to draw a shadow if we don't have one.
        if (mSlidableView == null) {
            return;
        }

        final int right = mSlidableView.getRight();
        final int top = mSlidableView.getTop() - mShadowHeight;
        final int bottom = mSlidableView.getTop();
        final int left = mSlidableView.getLeft();

        if (mShadowDrawable != null) {
            mShadowDrawable.setBounds(left, top, right, bottom);
            mShadowDrawable.draw(canvas);
        }
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof MarginLayoutParams
                ? new LayoutParams((MarginLayoutParams) layoutParams)
                : new LayoutParams(layoutParams);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams && super.checkLayoutParams(layoutParams);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putBoolean("isExpanded", isSlideable() ? isExpanded() : mPreservedExpandedState);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mPreservedExpandedState = bundle.getBoolean("isExpanded");
            mSlideOffset = mPreservedExpandedState ? EXPANDED : COLLAPSED;
            super.onRestoreInstanceState(bundle.getParcelable("instanceState"));
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    private static class LayoutParams extends ViewGroup.MarginLayoutParams {
        private static final int[] ATTRS = new int[]{
                android.R.attr.layout_weight
        };
        /**
         * True if this view is the slideable dragger in the layout.
         */
        private boolean slideable;

        public LayoutParams() {
            super(MATCH_PARENT, MATCH_PARENT);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            c.obtainStyledAttributes(attrs, ATTRS).recycle();
        }

    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return !mIsUnableToDrag && ((SlidingUpPanelLayout.LayoutParams) child.getLayoutParams()).slideable;
        }

        @Override
        public void onViewDragStateChanged(int state) {
            if (mDragHelper.getViewDragState() == ViewDragHelper.STATE_IDLE) {
                if (isExpanded()) {
                    updateObscuredViewVisibility();
                    mPreservedExpandedState = true;
                    if (mPanelSlideListener != null) {
                        mPanelSlideListener.onPanelExpanded(getDraggerView());
                    }
                } else {
                    mPreservedExpandedState = false;
                    if (mPanelSlideListener != null) {
                        mPanelSlideListener.onPanelCollapsed(getDraggerView());
                    }
                }

                sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
                requestLayout();
            }
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            // Make all child views visible in preparation for sliding things around
            setAllChildrenVisible();
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            final int topBound = getTopDistance(0);
            mSlideOffset = (float) (top - topBound) / mSlideRange;

            if (mPanelSlideListener != null) {
                mPanelSlideListener.onPanelSlide(getDraggerView(), mSlideOffset);
            }

            invalidate();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int top = getTopDistance(0);
            if (yvel > 0 || (yvel == 0 && mSlideOffset > 0.5f)) {
                top += mSlideRange;
            }

            mDragHelper.settleCapturedViewAt(releasedChild.getLeft(), top);
            invalidate();
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return mSlideRange;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            final int topBound = getTopDistance(0);
            final int bottomBound = topBound + mSlideRange;
            return Math.min(Math.max(top, topBound), bottomBound);
        }

    }

    /**
     * Listener for monitoring events about sliding panes.
     */
    public interface PanelSlideListener {
        /**
         * Called when a sliding pane's position changes.
         * @param panel The child view that was moved
         * @param slideOffset The new offset of this sliding pane within its range, from 0-1
         */
        public void onPanelSlide(View panel, float slideOffset);
        /**
         * Called when a sliding pane becomes slid completely collapsed. The pane may or may not
         * be interactive at this point depending on if it's shown or hidden
         * @param panel The child view that was slid to an collapsed position, revealing other panes
         */
        public void onPanelCollapsed(View panel);

        /**
         * Called when a sliding pane becomes slid completely expanded. The pane is now guaranteed
         * to be interactive. It may now obscure other views in the layout.
         * @param panel The child view that was slid to a expanded position
         */
        public void onPanelExpanded(View panel);
    }
}