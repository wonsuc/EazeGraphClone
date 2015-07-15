package com.example.w.eazegraphclone.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.w.eazegraphclone.R;
import com.example.w.eazegraphclone.charts.StackedBarChart;
import com.example.w.eazegraphclone.communication.IOnBarClickedListener;
import com.example.w.eazegraphclone.models.BarModel;
import com.example.w.eazegraphclone.models.StackedBarModel;


public class MainActivity extends ActionBarActivity {

    private StackedBarChart mStackedBarChart;

    public MainActivity() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_stacked_bar_chart);

        mStackedBarChart = (StackedBarChart) findViewById(R.id.stackedbarchart);
        mStackedBarChart.setOnBarClickedListener(new IOnBarClickedListener() {
            @Override
            public void onBarClicked(int _Position) {
                Log.d("StackedBarChart", "Position: " + _Position);
            }
        });
        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        //mStackedBarChart.startAnimation();
    }

    private void loadData() {
//        StackedBarModel s1 = new StackedBarModel("12.4");
//
//        s1.addBar(new BarModel(2.3f, 0xFF63CBB0));
//        s1.addBar(new BarModel(2.3f, 0xFF56B7F1));
//        s1.addBar(new BarModel(2.3f, 0xFFCDA67F));

        StackedBarModel s2 = new StackedBarModel("13.4");
        s2.addBar(new BarModel(1.1f, 0xFF63CBB0));
        s2.addBar(new BarModel(2.7f, 0xFF56B7F1));
        s2.addBar(new BarModel(0.7f, 0xFFCDA67F));

        StackedBarModel s3 = new StackedBarModel("14.4");

        s3.addBar(new BarModel(2.3f, 0xFF63CBB0));
        s3.addBar(new BarModel(2.f, 0xFF56B7F1));
        s3.addBar(new BarModel(3.3f, 0xFFCDA67F));

        StackedBarModel s4 = new StackedBarModel("15.4");
        s4.addBar(new BarModel(1.f, 0xFF63CBB0));
        s4.addBar(new BarModel(4.2f, 0xFF56B7F1));
        s4.addBar(new BarModel(2.1f, 0xFFCDA67F));

        StackedBarModel s5 = new StackedBarModel("16.4");

        s5.addBar(new BarModel(32.3f, 0xFF63CBB0));
        s5.addBar(new BarModel(12.f, 0xFF56B7F1));
        s5.addBar(new BarModel(22.3f, 0xFFCDA67F));

        StackedBarModel s6 = new StackedBarModel("17.4");
        s6.addBar(new BarModel(3.f, 0xFF63CBB0));
        s6.addBar(new BarModel(.7f, 0xFF56B7F1));
        s6.addBar(new BarModel(1.7f, 0xFFCDA67F));

        StackedBarModel s7 = new StackedBarModel("18.4");

        s7.addBar(new BarModel(2.3f, 0xFF63CBB0));
        s7.addBar(new BarModel(2.f, 0xFF56B7F1));
        s7.addBar(new BarModel(3.3f, 0xFFCDA67F));

        StackedBarModel s8 = new StackedBarModel("19.4");
        s8.addBar(new BarModel(5.4f, 0xFF63CBB0));
        s8.addBar(new BarModel(2.7f, 0xFF56B7F1));
        s8.addBar(new BarModel(3.4f, 0xFFCDA67F));

//        mStackedBarChart.addBar(s1);
        mStackedBarChart.addBar(s2);
        mStackedBarChart.addBar(s3);
        mStackedBarChart.addBar(s4);
        mStackedBarChart.addBar(s5);
        mStackedBarChart.addBar(s6);
        mStackedBarChart.addBar(s7);
        mStackedBarChart.addBar(s8);
    }

}
