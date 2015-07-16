/**
 *
 *   Copyright (C) 2015 Paul Cech
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.example.w.eazegraphclone.charts;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.OverScroller;
import android.widget.Scroller;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;

import com.example.w.eazegraphclone.R;
import com.example.w.eazegraphclone.communication.IOnBarClickedListener;
import com.example.w.eazegraphclone.models.BaseModel;
import com.example.w.eazegraphclone.utils.Utils;

import java.util.List;

/**
 * The abstract class for every type of bar chart, which handles the general calculation for the bars.
 */
public abstract class BaseBarChart extends BaseChart {

    //##############################################################################################
    // Variables
    //##############################################################################################

    private static final String LOG_TAG = BaseBarChart.class.getSimpleName();

    // All float values are dp values and will be converted into px values in the constructor
    public static final boolean DEF_SHOW_VALUES         = true;
    public static final float   DEF_BAR_WIDTH           = 32.f;
    public static final boolean DEF_FIXED_BAR_WIDTH     = false;
    public static final float   DEF_BAR_MARGIN          = 12.f;
    public static final boolean DEF_SCROLL_ENABLED      = true;
    public static final int     DEF_VISIBLE_BARS        = 6;

    /**
     * The current viewport. This rectangle represents the currently visible chart domain
     * and range. The currently visible chart X values are from this rectangle's left to its right.
     * The currently visible chart Y values are from this rectangle's top to its bottom.
     * <p>
     * Note that this rectangle's top is actually the smaller Y value, and its bottom is the larger
     * Y value. Since the chart is drawn onscreen in such a way that chart Y values increase
     * towards the top of the screen (decreasing pixel Y positions), this rectangle's "top" is drawn
     * above this rectangle's "bottom" value.
     *
     * @see #mContentRect
     */
    protected RectF mCurrentViewport = new RectF();

    /**
     * The current destination rectangle (in pixel coordinates) into which the chart data should
     * be drawn. Chart labels are drawn outside this area.
     *
     * @see #mCurrentViewport
     */
    protected Rect mContentRect = new Rect();

    protected IOnBarClickedListener mListener = null;

    protected Paint           mGraphPaint;
    protected Paint           mLegendPaint;

    protected float           mBarWidth;
    protected boolean         mFixedBarWidth;
    protected float           mBarMargin;
    protected int             mAvailableScreenSize;

    protected int             mVisibleBars;
    protected boolean         mShowValues;

    public BaseBarChart(Context context) {
        super(context);

        mShowValues         = DEF_SHOW_VALUES;
        mBarWidth           = Utils.dpToPx(DEF_BAR_WIDTH);
        mBarMargin          = Utils.dpToPx(DEF_BAR_MARGIN);
        mFixedBarWidth      = DEF_FIXED_BAR_WIDTH;
        mVisibleBars        = DEF_VISIBLE_BARS;
    }

    public BaseBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.BaseBarChart,
                0, 0
        );
        try {
            mShowValues         = a.getBoolean(R.styleable.BaseBarChart_egShowValues,         DEF_SHOW_VALUES);
            mBarWidth           = a.getDimension(R.styleable.BaseBarChart_egBarWidth,         Utils.dpToPx(DEF_BAR_WIDTH));
            mBarMargin          = a.getDimension(R.styleable.BaseBarChart_egBarMargin,        Utils.dpToPx(DEF_BAR_MARGIN));
            mFixedBarWidth      = a.getBoolean(R.styleable.BaseBarChart_egFixedBarWidth,      DEF_FIXED_BAR_WIDTH);
            mVisibleBars        = a.getInt(R.styleable.BaseBarChart_egVisibleBars,            DEF_VISIBLE_BARS);
        } finally {
            // release the TypedArray so that it can be reused.
            a.recycle();
        }
    }

    public IOnBarClickedListener getOnBarClickedListener() {
        return mListener;
    }
    public void setOnBarClickedListener(IOnBarClickedListener _listener) {
        mListener = _listener;
    }

    public float getBarWidth() {
        return mBarWidth;
    }
    public void setBarWidth(float _barWidth) {
        mBarWidth = _barWidth;
        onDataChanged();
    }

    /**
     * Bar가 고정된 넓이를 가지고 있는지 아니면 계산된 넓이를 가지고 있는지를 boolean으로 반환
     * @return
     */
    public boolean isFixedBarWidth() {
        return mFixedBarWidth;
    }

    /**
     * Sets if the bar width should be fixed or dynamically caluclated
     * @param _fixedBarWidth True if it should be a fixed width.
     */
    public void setFixedBarWidth(boolean _fixedBarWidth) {
        mFixedBarWidth = _fixedBarWidth;
        onDataChanged();
    }

    /**
     * Returns the bar margin, which is set by user if the bar widths are calculated dynamically.
     * @return
     */
    public float getBarMargin() {
        return mBarMargin;
    }

    /**
     * Sets the bar margin.
     * @param _barMargin Bar margin
     */
    public void setBarMargin(float _barMargin) {
        mBarMargin = _barMargin;
        onDataChanged();
    }

    public int getVisibleBars() {
        return mVisibleBars;
    }

    public void setVisibleBars(int _visibleBars) {
        mVisibleBars = _visibleBars;
        onDataChanged();
    }

    /**
     * Determines if the values of each data should be shown in the graph.
     * @param _showValues true to show values in the graph.
     */
    public void setShowValues(boolean _showValues) {
        mShowValues = _showValues;
        invalidateGlobal();
    }

    /**
     * Returns if the values are drawn on top of the bars.
     * @return True if they are drawn.
     */
    public boolean isShowValues() {
        return mShowValues;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Check if the current graph is a VerticalBarChart and set the
        // availableScreenSize to the chartHeight
        mAvailableScreenSize = mGraphWidth;

        if(getData().size() > 0) {
            onDataChanged();
        }
    }

    /**
     * This is the main entry point after the graph has been inflated. Used to initialize the graph
     * and its corresponding members.
     */
    @Override
    protected void initializeGraph() {
        super.initializeGraph();

        mGraphPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGraphPaint.setStyle(Paint.Style.FILL);

        mLegendPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        mLegendPaint.setColor(mLegendColor);
        mLegendPaint.setTextSize(mLegendTextSize);
        mLegendPaint.setStrokeWidth(2);
        mLegendPaint.setStyle(Paint.Style.FILL);

        mMaxFontHeight = Utils.calculateMaxTextHeight(mLegendPaint, null);

    }

    /**
     * Calculates the bar width and bar margin based on the _DataSize and settings and starts the boundary
     * calculation in child classes.
     * @param _DataSize Amount of data sets
     */
    protected void calculateBarPositions(int _DataSize) {

        int   dataSize = _DataSize;
        float barWidth = mBarWidth;
        float margin   = mBarMargin;

        if (!mFixedBarWidth) {
            // calculate the bar width if the bars should be dynamically displayed
            barWidth = (mAvailableScreenSize / _DataSize) - margin;
        } else {

            if(_DataSize < mVisibleBars) {
                dataSize = _DataSize;
            }

            // calculate margin between bars if the bars have a fixed width
            float cumulatedBarWidths = barWidth * dataSize;
            float remainingScreenSize = mAvailableScreenSize - cumulatedBarWidths;

            margin = remainingScreenSize / dataSize;
        }

        int calculatedSize = (int) ((barWidth * _DataSize) + (margin * _DataSize));
        int contentWidth   = calculatedSize;
        int contentHeight  = mGraphHeight;

        mContentRect       = new Rect(0, 0, contentWidth, contentHeight);
        mCurrentViewport   = new RectF(0, 0, mGraphWidth, mGraphHeight);

        calculateBounds(barWidth, margin);
        mLegend.invalidate();
        mGraph.invalidate();
    }

    /**
     * Calculates the bar boundaries based on the bar width and bar margin.
     * @param _Width    Calculated bar width
     * @param _Margin   Calculated bar margin
     */
    protected abstract void calculateBounds(float _Width, float _Margin);

    /**
     * Callback method for drawing the bars in the child classes.
     * @param _Canvas The canvas object of the graph view.
     */
    protected abstract void drawBars(Canvas _Canvas);

    /**
     * Returns the list of data sets which hold the information about the legend boundaries and text.
     * @return List of BaseModel data sets.
     */
    protected abstract List<? extends BaseModel> getLegendData();

    protected abstract List<RectF> getBarBounds();

    // ---------------------------------------------------------------------------------------------
    //                          Override methods from view layers
    // ---------------------------------------------------------------------------------------------

    //region Override Methods
    @Override
    protected void onGraphDraw(Canvas _Canvas) {
        super.onGraphDraw(_Canvas);
        _Canvas.translate(-mCurrentViewport.left, -mCurrentViewport.top);
        drawBars(_Canvas);
    }

    @Override
    protected void onLegendDraw(Canvas _Canvas) {
        super.onLegendDraw(_Canvas);

        _Canvas.translate(-mCurrentViewport.left, 0);

        for (BaseModel model : getLegendData()) {
            if(model.canShowLabel()) {
                RectF bounds = model.getLegendBounds();
                _Canvas.drawText(model.getLegendLabel(), model.getLegendLabelPosition(), bounds.bottom - mMaxFontHeight, mLegendPaint);
//                _Canvas.drawLine(
//                        bounds.centerX(),
//                        bounds.bottom - mMaxFontHeight * 2 - mLegendTopPadding,
//                        bounds.centerX(),
//                        mLegendTopPadding, mLegendPaint
//                );
            }
        }
    }

    // 특정 Bar를 터치했을 때의 Callback listener
    @Override
    protected boolean onGraphOverlayTouchEvent(MotionEvent _Event) {
        boolean result = false;

        switch (_Event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                result = true;

                if (mListener == null) {
                    // 리스너가 세팅되어 있지 않으면 View의 터치 이벤트 발생시킴
                    BaseBarChart.this.onTouchEvent(_Event);
                } else {
                    float newX = _Event.getX() + mCurrentViewport.left;
                    float newY = _Event.getY() + mCurrentViewport.top;
                    int   counter = 0;

                    for (RectF rectF : getBarBounds()) {
                        // Bar의 Bounds를 모두 가져와서 터치한 포인트와 교차하는지를 체크
                        if (Utils.intersectsPointWithRectF(rectF, newX, newY)) {
                            mListener.onBarClicked(counter);
                            break;
                        }
                        counter++;
                    }
                }
                break;
        }

        return result;
    }

    //endregion
}
