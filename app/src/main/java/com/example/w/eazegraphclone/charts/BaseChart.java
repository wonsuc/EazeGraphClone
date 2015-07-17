/**
 *
 *   Copyright (C) 2014 Paul Cech
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
import android.graphics.Matrix;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.w.eazegraphclone.R;
import com.example.w.eazegraphclone.models.BaseModel;
import com.example.w.eazegraphclone.utils.Utils;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;


/**
 * This is the main chart class and should be inherited by every graph. This class provides some general
 * methods and variables, which are needed and used by every type of chart.
 */
public abstract class BaseChart extends ViewGroup {

    //##############################################################################################
    // Variables
    //##############################################################################################

    protected final static NumberFormat mFormatter = NumberFormat.getInstance(Locale.getDefault());

    public static final float   DEF_LEGEND_HEIGHT       = 58.f;
    public static final int     DEF_LEGEND_COLOR        = 0xFF898989;
    // will be interpreted as sp value
    public static final float   DEF_LEGEND_TEXT_SIZE    = 12.f;
    public static final boolean DEF_SHOW_DECIMAL        = false;
    public static final String  DEF_EMPTY_DATA_TEXT     = "No Data available";

    protected Graph             mGraph;
    protected Legend            mLegend;

    protected int               mHeight;
    protected int               mWidth;

    protected int               mGraphWidth;
    protected int               mGraphHeight;

    protected float             mLegendWidth;
    protected float             mLegendHeight;
    protected float             mLegendTextSize;
    protected int               mLegendColor;

    protected int               mLeftPadding;
    protected int               mTopPadding;
    protected int               mRightPadding;
    protected int               mBottomPadding;

    protected String            mEmptyDataText;

    protected float             mMaxFontHeight;
    protected float             mLegendTopPadding = Utils.dpToPx(4.f);

    protected boolean           mShowDecimal;

    protected BaseChart(Context context) {
        super(context);

        mLegendHeight   = Utils.dpToPx(DEF_LEGEND_HEIGHT);
        mLegendTextSize = Utils.dpToPx(DEF_LEGEND_TEXT_SIZE);
        mLegendColor    = DEF_LEGEND_COLOR;
        mShowDecimal    = DEF_SHOW_DECIMAL;
        mEmptyDataText  = DEF_EMPTY_DATA_TEXT;
    }

    public BaseChart(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.BaseChart,
                0, 0
        );

        try {

            mLegendHeight       = a.getDimension(R.styleable.BaseChart_egLegendHeight,     Utils.dpToPx(DEF_LEGEND_HEIGHT));
            mLegendTextSize     = a.getDimension(R.styleable.BaseChart_egLegendTextSize,   Utils.dpToPx(DEF_LEGEND_TEXT_SIZE));
            mShowDecimal        = a.getBoolean(R.styleable.BaseChart_egShowDecimal,        DEF_SHOW_DECIMAL);
            mLegendColor        = a.getColor(R.styleable.BaseChart_egLegendColor,          DEF_LEGEND_COLOR);
            mEmptyDataText      = a.getString(R.styleable.BaseChart_egEmptyDataText);

        } finally {
            // release the TypedArray so that it can be reused.
            a.recycle();
        }

        if(mEmptyDataText == null) {
            mEmptyDataText = DEF_EMPTY_DATA_TEXT;
        }

    }

    /**
     * Returns the current height of the legend view
     * @return Legend view height
     */
    public float getLegendHeight() {
        return mLegendHeight;
    }

    /**
     * Sets and updates the height of the legend view.
     * @param _legendHeight The new legend view height.
     */
    public void setLegendHeight(float _legendHeight) {
        mLegendHeight = Utils.dpToPx(_legendHeight);

        if(getData().size() > 0)
            onDataChanged();
    }

    /**
     * Legend Text의 사이즈를 반환한다.
     * @return Size of the legend text.
     */
    public float getLegendTextSize() {
        return mLegendTextSize;
    }

    /**
     * Legen Text의 사이즈를 설정한다.
     * @param _legendTextSize Size of the legend text.
     */
    public void setLegendTextSize(float _legendTextSize) {
        mLegendTextSize = Utils.dpToPx(_legendTextSize);
    }

    public boolean isShowDecimal() {
        return mShowDecimal;
    }

    public void setShowDecimal(boolean _showDecimal) {
        mShowDecimal = _showDecimal;
        invalidate();
    }

    public int getLegendColor() {
        return mLegendColor;
    }
    public void setLegendColor(int _legendColor) {
        mLegendColor = _legendColor;
    }

    public String getEmptyDataText() {
        return mEmptyDataText;
    }
    public void setEmptyDataText(String _emptyDataText) {
        mEmptyDataText = _emptyDataText;
    }

    /**
     * View를 Reload하며 모든 것이 다시 그려진다.
     */
    public void reloadView () {
        invalidateGlobal();
    }

    /**
     * Returns the datasets which are currently inserted.
     * @return the datasets
     */
    public abstract List<? extends BaseModel> getData();

    /**
     * Data object를 리셋하고 지운다.
     */
    public abstract void clearChart();

    /**
     * Should be called when the dataset changed and the graph should update and redraw.
     * Graph implementations might overwrite this method to do more work than just call onDataChanged()
     */
    public void update() {
        onDataChanged();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;

        mLeftPadding    = getPaddingLeft();
        mTopPadding     = getPaddingTop();
        mRightPadding   = getPaddingRight();
        mBottomPadding  = getPaddingBottom();

        mGraph.layout(mLeftPadding, mTopPadding, w - mRightPadding, (int) (h - mLegendHeight - mBottomPadding));
        mGraph.setBackgroundColor(getResources().getColor(android.R.color.holo_purple));

        Log.d(VIEW_LOG_TAG, "onSizeChanged, mGraphOverlay.layout(" +
                "l: " + mLeftPadding + ", t: " + mTopPadding + ", r: " + (w - mRightPadding) + ", b: " + ((int) (h - mLegendHeight - mBottomPadding)) + ") <= Pixel");

        mLegend.layout(mLeftPadding, (int) (h - mLegendHeight - mBottomPadding), w - mRightPadding, h - mBottomPadding);
    }

    /**
     * Graph가 xml로부터 inflate되고 난 이후에 실행되는 Entry point 메서드. 그래프를 초기화하고 이에 해당하는
     * 멤버필드를 초기화 하기 위해 사용되었다.
     */
    protected void initializeGraph() {
        mGraph = new Graph(getContext());
        addView(mGraph);

        mLegend = new Legend(getContext());
        addView(mLegend);
    }

    /**
     * 새로운 데이타가 삽입되었을 때. 그리고 View의 Dimension이 변경되었을 때 자동으로 호출된다.
     */
    protected void onDataChanged() {
        invalidateGlobal();
    }

    /**
     * Invalidates graph and legend and forces them to be redrawn.
     */
    protected final void invalidateGlobal() {
        mGraph.invalidate();
        mLegend.invalidate();
    }

    protected final void invalidateGraph() {
        mGraph.invalidate();
    }
    protected final void invalidateLegend() {
        mLegend.invalidate();
    }

    // #############################################################################################
    //                          Override methods from view layers
    // ##############################################################################################

    protected void onGraphDraw(Canvas _Canvas) {

    }

    protected void onLegendDraw(Canvas _Canvas) {

    }

    protected void onGraphSizeChanged(int w, int h, int oldw, int oldh) {

    }

    protected void onLegendSizeChanged(int w, int h, int oldw, int oldh) {

    }

    //##############################################################################################
    // Graph
    //##############################################################################################

    protected class Graph extends View {
        private Matrix mTransform = new Matrix();
        private Graph(Context context) {
            super(context);
        }
        public void accelerate() {
            Utils.setLayerToHW(this);
        } // (consumes memory)
        public void decelerate() {
            Utils.setLayerToSW(this);
        } // (releases memory)
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (Build.VERSION.SDK_INT < 11) {
                //mTransform.set(canvas.getMatrix());
                mTransform.set(getMatrix());
                //mTransform.preRotate(mRotation, mPivot.x, mPivot.y);
                canvas.setMatrix(mTransform);
            }
            onGraphDraw(canvas);
        }
        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            mGraphWidth = w;
            mGraphHeight = h;
            onGraphSizeChanged(w, h, oldw, oldh);
        }
        @Override
        public boolean performClick() {
            return super.performClick();
        }
    }

    //##############################################################################################
    // Legend
    //##############################################################################################

    protected class Legend extends View {
        private Legend(Context context) {
            super(context);
        }
        public void accelerate() {
            Utils.setLayerToHW(this);
        } // (consumes memory)
        public void decelerate() {
            Utils.setLayerToSW(this);
        } // (releases memory)
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            onLegendDraw(canvas);
        }
        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            mLegendWidth = w;
            mLegendHeight = h;
            onLegendSizeChanged(w, h, oldw, oldh);
        }
    }

}
