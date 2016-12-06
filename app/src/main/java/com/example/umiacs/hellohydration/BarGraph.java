package com.example.umiacs.hellohydration;

import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

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
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;


import java.util.ArrayList;

/**
 * Created by umiacs on 12/1/16.
 */

public class BarGraph extends AppCompatActivity {
    BarChart barChart;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bargraph);

        barChart = (BarChart) findViewById(R.id.bargraph);
        barChart.setNoDataText("Start drinking!");
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(1, 4.5f));
        barEntries.add(new BarEntry(2, 8f));
        barEntries.add(new BarEntry(3, 6f));
        barEntries.add(new BarEntry(4, 12f));
        barEntries.add(new BarEntry(5, 18f));
        barEntries.add(new BarEntry(6, 9f));
        barEntries.add(new BarEntry(7, 20f));

        BarDataSet barDataSet = new BarDataSet(barEntries, "Water Consumption");
        //barDataSet.addColor(Color.BLUE);


        BarData barData = new BarData(barDataSet);
        barChart.setFitBars(true);
        // barChart.setFitBars(true);
        barData.setBarWidth(.96f);

        //barChart.setDragEnabled(false);
        barChart.setData(barData);
        // barChart.setTouchEnabled(true);

        //  barChart.setMaxVisibleValueCount(7);

        Legend legend = barChart.getLegend();
        legend.setEnabled(false);

        barChart.setScaleEnabled(true);
        XAxis xAxis = barChart.getXAxis();

        String[] xLabels = {"", "Su", "M", "T", "W", "Th", "F", "Sa"};

        xAxis.setValueFormatter(new MyAxisFormatter(xLabels));
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        //xAxis.setEnabled(false);
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


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("BarGraph Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    private class MyAxisFormatter implements IAxisValueFormatter {

        private String[] xLabels;

        public MyAxisFormatter(String[] values) {
            xLabels = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return xLabels[(int) value];
        }
    }

}
