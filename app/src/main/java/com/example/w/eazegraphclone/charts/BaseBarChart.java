package com.example.w.eazegraphclone.charts;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.example.w.eazegraphclone.R;
import com.example.w.eazegraphclone.models.BaseModel;
import com.example.w.eazegraphclone.utils.Utils;

import java.util.List;

/**
 * 모든 타입의 바 차트를 위한 추상 클래스. 바들의 일반적인 계산을 핸들한다.
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
     * Calculates the bar width and bar margin based on the _DataSize and settings and starts the boundary calculation in child classes.
     * @param _DataSize Amount of data sets
     */
    protected void calculateBarPositions(int _DataSize) {
        // _DataSize는 StackedBarChart의 갯수를 의미한다.
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
            // cumulatedBarWidths는 바의 넓이와 바의 갯수를 곱한 값이다.
            float cumulatedBarWidths = barWidth * dataSize;
            // remainingScreenSize는 총 스크린 넓이에서 그려진 바의 총 넓이를 빼고 남은 영역이다.
            float remainingScreenSize = mAvailableScreenSize - cumulatedBarWidths;

            // 남은 영역들을 StackedBar의 갯수만큼 쪼갠다.
            margin = remainingScreenSize / dataSize;
        }

        // barWidth에 _DataSize를 곱한 값과 margin에 _DataSize 곱한 값을 더한다. 따라서 contentWidth는 모든 ???
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
        // 실제로 Bar를 그리는 것은 StackedBarChart이다.
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

    //endregion
}
