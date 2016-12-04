package com.example.umiacs.hellohydration;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;


import java.util.ArrayList;

/**
 * Created by umiacs on 12/1/16.
 */

public class BarGraph extends AppCompatActivity {
    BarChart barChart;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bargraph);

        barChart = (BarChart) findViewById(R.id.bargraph);
        barChart.setNoDataText("Start drinking!");
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0, 4.5f));
        barEntries.add(new BarEntry(1, 8f));
        barEntries.add(new BarEntry(2, 6f));
        barEntries.add(new BarEntry(3, 12f));
        barEntries.add(new BarEntry(4,18f));
        barEntries.add(new BarEntry(5, 9f));
        BarDataSet barDataSet = new BarDataSet(barEntries, "Water Consumption");
        //barDataSet.addColor(Color.BLUE);




        BarData barData = new BarData(barDataSet);
       // barChart.setFitBars(true);
        barData.setBarWidth(.96f);
        barChart.setFitBars(true);
        //barChart.setDragEnabled(false);
        barChart.setData(barData);
       // barChart.setTouchEnabled(true);

      //  barChart.setMaxVisibleValueCount(7);

        Legend legend = barChart.getLegend();
        legend.setEnabled(false);


        barChart.setScaleEnabled(true);
        XAxis xAxis = barChart.getXAxis();

        String[] xLabels = {"Su","M", "T", "W","Th", "F", "Sa"};

        xAxis.setValueFormatter(new MyAxisFormatter(xLabels));
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setAxisMinimum(0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yLeftAxis = barChart.getAxisLeft();
        YAxis yRightAxis = barChart.getAxisRight();
        barChart.getAxisRight().setEnabled(false);
        yLeftAxis.setAxisMinimum(0f);
        yLeftAxis.setDrawAxisLine(true);
        yLeftAxis.setLabelCount(6, true);
        yLeftAxis.setDrawGridLines(true); //  grid lines
        yRightAxis.setDrawGridLines(false); // no grid lines
        barChart.setDescription(null);
        barChart.invalidate();


    }
    private class MyAxisFormatter implements IAxisValueFormatter{

        private String[] xLabels;

        public MyAxisFormatter(String[] values){
            xLabels = values;
        }
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return xLabels[(int) value];
        }
    }

}
