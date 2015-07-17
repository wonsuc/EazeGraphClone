package com.example.w.eazegraphclone.charts;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.example.w.eazegraphclone.R;
import com.example.w.eazegraphclone.models.BarModel;
import com.example.w.eazegraphclone.models.BaseModel;
import com.example.w.eazegraphclone.models.StackedBarModel;
import com.example.w.eazegraphclone.utils.Utils;

import java.util.ArrayList;
import java.util.List;


/**
 * A rather simple type of a bar chart, where all the bars have the same height and their inner bars
 * heights are dependent on each other.
 */
public class StackedBarChart extends BaseBarChart {

    //##############################################################################################
    // Variables
    //##############################################################################################

    private static final String LOG_TAG = StackedBarChart.class.getSimpleName();

    public static final float   DEF_TEXT_SIZE       = 12f;

    private Paint                  mTextPaint;

    private List<StackedBarModel>  mData;

    private float                  mTextSize;

    public StackedBarChart(Context context) {
        super(context);

        mTextSize       = Utils.dpToPx(DEF_TEXT_SIZE);

        initializeGraph();
    }

    public StackedBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.StackedBarChart,
                0, 0
        );
        try {
            mTextSize       = a.getDimension(R.styleable.StackedBarChart_egBarTextSize,     Utils.dpToPx(DEF_TEXT_SIZE));
        } finally {
            // release the TypedArray so that it can be reused.
            a.recycle();
        }
        initializeGraph();
    }

    /**
     * Returns the text size for the values which are shown in the bars.
     * @return The text size in px
     */
    public float getTextSize() {
        return mTextSize;
    }

    /**
     * Sets the text size for the values which are shown in the bars.
     * @param _textSize Size in sp
     */
    public void setTextSize(float _textSize) {
        mTextSize = Utils.dpToPx(_textSize);
        onDataChanged();
    }

    /**
     * Adds a new {@link StackedBarModel} to the BarChart.
     * @param _Bar The StackedBarModel which will be added to the chart.
     */
    public void addBar(StackedBarModel _Bar) {
        mData.add(_Bar);
        onDataChanged();
    }

    /**
     * Adds a new list of {@link StackedBarModel} to the BarChart.
     * @param _List The StackedBarModel list which will be added to the chart.
     */
    public void addBarList(List<StackedBarModel> _List) {
        mData = _List;
        onDataChanged();
    }

    /**
     * Returns the data which is currently present in the chart.
     * @return The currently used data.
     */
    @Override
    public List<StackedBarModel> getData() {
        return mData;
    }

    /**
     * Resets and clears the data object.
     */
    @Override
    public void clearChart() {
        mData.clear();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            performClick();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Graph가 xml로부터 inflate되고 난 이후에 실행되는 Entry point 메서드. 그래프를 초기화하고 이에 해당하는
     * 멤버필드를 초기화 하기 위해 사용되었다.
     */
    @Override
    protected void initializeGraph() {
        super.initializeGraph();
        mData = new ArrayList<>();

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(0xFFFFFFFF);

        if(this.isInEditMode()) {
            StackedBarModel s1 = new StackedBarModel();

            s1.addBar(new BarModel(2.3f, 0xFF123456));
            s1.addBar(new BarModel(2.f,  0xFF1EF556));
            s1.addBar(new BarModel(3.3f, 0xFF1BA4E6));

            StackedBarModel s2 = new StackedBarModel();
            s2.addBar(new BarModel(1.1f, 0xFF123456));
            s2.addBar(new BarModel(2.7f, 0xFF1EF556));
            s2.addBar(new BarModel(0.7f, 0xFF1BA4E6));

            addBar(s1);
            addBar(s2);
        }
    }

    /**
     * 새로운 데이타가 삽입되었을 때. 그리고 View의 Dimension이 변경되었을 때 자동으로 호출된다.
     */
    @Override
    protected void onDataChanged() {
        calculateBarPositions(mData.size());
        super.onDataChanged();
    }

    /**
     * Calculates the bar boundaries based on the bar width and bar margin.
     * @param _Width    Calculated bar width
     * @param _Margin   Calculated bar margin
     */
    protected void calculateBounds(float _Width, float _Margin) {

        int last = 0;
        // 최상위 Loop문
        for (StackedBarModel model : mData) {
            float lastY = 0;
            // 하나의 StackedBar의 값들을 모두 더한 값
            float cumulatedValues = 0;

            // used if seperators are enabled, to prevent information loss
            // StackedBar의 갯수에서 1을 뺀 값(예를 들어 3개일 때는 2개)에서 Separator의 넓이를 곱한 값을 총 그래프 높이에서 뺀 값이 실제 보여지는 그래프의 높이 값임.
            Log.d(LOG_TAG, "calculateBounds, mGraphHeight: " + mGraphHeight);

            for (BarModel barModel : model.getBars()) {
                cumulatedValues += barModel.getValue();
            }

            last += _Margin / 2;

            // Loop문
            for (BarModel barModel : model.getBars()) {
                // calculate topX for the StackedBarModel part
                // 가장 높은 그래프의 값을 정해서 그 값을 기준으로 나눠야 함. 아래 코드의 경우 usableGraphHeight 가장 높은 값으로 변경
                //float newY = ((barModel.getValue() * usableGraphHeight) / cumulatedValues) + lastY;
                // 하나의 바의 높이를 구한 뒤 거기에 사용가능한 바 그래프의 높이를 곱한다. 거기서 모든 값들을 더한 값으로 나눈다.
                // 그리고 LastY를 구하는데 LastY는 StackedBar에서 이전 바의 top 값이다.
                float newY = ((barModel.getValue() * mGraphHeight) / cumulatedValues) + lastY;
                Log.d(LOG_TAG, "calculateBounds, newY: " + newY);
                float height = newY - lastY;
                Rect textBounds = new Rect();
                // 해당 Bar의 값
                String value = String.valueOf(barModel.getValue());

                mTextPaint.getTextBounds(value, 0, value.length(), textBounds);
                //Log.d(LOG_TAG, "calculateBounds, textBounds: " + textBounds);

                if (textBounds.height() * 1.5f < height && textBounds.width() * 1.1f < _Width) {
                    barModel.setShowValue(true);
                    // ??
                    barModel.setValueBounds(textBounds);
                }

                barModel.setBarBounds(new RectF(last, lastY, last + _Width, newY));
                Log.d(LOG_TAG, "calculateBounds, barModel.getBarBounds(): " + barModel.getBarBounds());
                // ??
                lastY = newY;
            }
            model.setLegendBounds(new RectF(last, 0, last + _Width, mLegendHeight));

            last += _Width + (_Margin / 2);
        }

        Utils.calculateLegendInformation(mData, 0, mContentRect.width(), mLegendPaint);
    }

    /**
     * Callback method for drawing the bars in the child classes.
     * @param _Canvas 그래프 뷰의 Canvas 객체
     */
    protected void drawBars(Canvas _Canvas) {
        for (StackedBarModel model : mData) {
            float lastTop;
            float lastBottom = mGraphHeight;

            for (int index = 0; index < model.getBars().size(); index++) {
                BarModel barModel = model.getBars().get(index);

                RectF bounds = barModel.getBarBounds();
                mGraphPaint.setColor(barModel.getColor());

                float height = (bounds.height());
                lastTop = lastBottom - height;

                _Canvas.drawRect(
                        bounds.left,
                        lastTop,
                        bounds.right,
                        lastBottom,
                        mGraphPaint
                );

                // mShowValues가 존재하고, isShowValue가 true일 때
                if (mShowValues && barModel.isShowValue()) {
                    _Canvas.drawText(
                            String.valueOf(barModel.getValue()),
                            bounds.centerX(),
                            (lastTop + height / 2) + barModel.getValueBounds().height()/2, // 바 높이의 절반 + ???
                            mTextPaint
                    );
                }

                lastBottom = lastTop;
            }

        }
    }

    /**
     * Returns the list of data sets which hold the information about the legend boundaries and text.
     * @return List of BaseModel data sets.
     */
    @Override
    protected List<? extends BaseModel> getLegendData() {
        return mData;
    }

    @Override
    protected List<RectF> getBarBounds() {
        ArrayList<RectF> bounds = new ArrayList<RectF>();
        for (StackedBarModel model : mData) {
            bounds.add(model.getBounds());
        }
        return bounds;
    }

}
