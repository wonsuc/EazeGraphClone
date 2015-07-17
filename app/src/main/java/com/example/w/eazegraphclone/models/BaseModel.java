package com.example.w.eazegraphclone.models;

import android.graphics.Rect;
import android.graphics.RectF;

/**
 * The BaseModel is the parent model of every chart model. It basically only holds the information
 * about the legend labels of the childs value.
 */
public abstract class BaseModel {

    /**
     * Label의 값
     */
    protected String mLegendLabel;

    /**
     * Label을 보여줄 것인지 여부
     */
    protected boolean mShowLabel;

    /**
     * 라벨의 X 좌표값
     */
    private int mLegendLabelPosition;

    /**
     * Label의 경계영역
     */
    private RectF mLegendBounds;

    /**
     * Legend Label들의 경계영역
     */
    private Rect mTextBounds;


    protected BaseModel(String _legendLabel) {
        mLegendLabel = _legendLabel;
    }

    protected BaseModel() {
    }


    public String getLegendLabel() {
        return mLegendLabel;
    }

    public void setLegendLabel(String _LegendLabel) {
        mLegendLabel = _LegendLabel;
    }


    public boolean canShowLabel() {
        return mShowLabel;
    }

    public void setShowLabel(boolean _showLabel) {
        mShowLabel = _showLabel;
    }


    public int getLegendLabelPosition() {
        return mLegendLabelPosition;
    }

    public void setLegendLabelPosition(int _legendLabelPosition) {
        mLegendLabelPosition = _legendLabelPosition;
    }


    public RectF getLegendBounds() {
        return mLegendBounds;
    }

    public void setLegendBounds(RectF _legendBounds) {
        mLegendBounds = _legendBounds;
    }


    public Rect getTextBounds() {
        return mTextBounds;
    }

    public void setTextBounds(Rect _textBounds) {
        mTextBounds = _textBounds;
    }

}
