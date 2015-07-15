package com.example.w.eazegraphclone.models;

import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link com.example.w.eazegraphclone.charts.StackedBarChart}를 위한 모델 클래스
 * {@link BarModel}에 대한 Simple wrapper class
 */
public class StackedBarModel extends BaseModel {

    /**
     * StackedBar내에 존재하는 Bar들
     */
    List<BarModel> mBars;

    public StackedBarModel() {
        super("Unset");
        mBars = new ArrayList<BarModel>();
    }

    public StackedBarModel(String _legendLabel) {
        super(_legendLabel);
        mBars = new ArrayList<BarModel>();
    }

    public StackedBarModel(List<BarModel> _bars) {
        super("Unset");
        mBars = _bars;
    }

    public StackedBarModel(String _legendLabel, List<BarModel> _bars) {
        super(_legendLabel);
        mBars = _bars;
    }

    public List<BarModel> getBars() {
        return mBars;
    }

    public void setBars(List<BarModel> _bars) {
        mBars = _bars;
    }

    public void addBar(BarModel _bar) {
        mBars.add(_bar);
    }

    public RectF getBounds() {
        RectF bounds = new RectF();
        if(!mBars.isEmpty()) {
            // get bounds from complete StackedBar
            bounds.set(
                    mBars.get(0).getBarBounds().left,
                    mBars.get(0).getBarBounds().top,
                    mBars.get(mBars.size() - 1).getBarBounds().right,
                    mBars.get(mBars.size() - 1).getBarBounds().bottom
            );
        }
        return bounds;
    }

}
