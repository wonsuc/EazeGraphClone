package com.example.w.eazegraphclone.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.example.w.eazegraphclone.R;
import com.example.w.eazegraphclone.charts.StackedBarChart;
import com.example.w.eazegraphclone.models.BarModel;
import com.example.w.eazegraphclone.models.StackedBarModel;


public class MainActivity extends ActionBarActivity {

    private StackedBarChart mStackedBarChart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_stacked_bar_chart);

        mStackedBarChart = (StackedBarChart) findViewById(R.id.stackedbarchart);
        loadData();
    }

    private void loadData() {

        StackedBarModel s1 = new StackedBarModel("그래프1");
        s1.addBar(new BarModel(50, 0xFF63CBB0));
        s1.addBar(new BarModel(20, 0xFF56B7F1));


        StackedBarModel s2 = new StackedBarModel("그래프2");
        s2.addBar(new BarModel(70, 0xFF63CBB0));
        s2.addBar(new BarModel(30, 0xFF56B7F1));


        mStackedBarChart.addBar(s1);
        mStackedBarChart.addBar(s2);
    }

}
