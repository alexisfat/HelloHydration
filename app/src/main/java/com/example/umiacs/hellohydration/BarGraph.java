package com.example.umiacs.hellohydration;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
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
 * Ciations:
 * PhilJay/MPAndroidChart - github repo to help make charts
 *
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
        /*Create the bar chart
         * If no data is recorded this should say "Start drinking!"
         */
        barChart = (BarChart) findViewById(R.id.bargraph);
        barChart.setNoDataText("Start drinking!");

        /*Varibale created to contain all the entries in this graph
        * Hard coded some data for the prototype
        * */
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(1, 4.5f));
        barEntries.add(new BarEntry(2, 8f));
        barEntries.add(new BarEntry(3, 6f));
        barEntries.add(new BarEntry(4, 12f));
        barEntries.add(new BarEntry(5, 18f));
        barEntries.add(new BarEntry(6, 9f));
        barEntries.add(new BarEntry(7, 20f));
        /*This created the data set to be plotted,
        * if legend was showed this would display water consumption*/
        BarDataSet barDataSet = new BarDataSet(barEntries, "Water Consumption");
        BarData barData = new BarData(barDataSet);
        /*Makes all the bars the same size*/
        barChart.setFitBars(true);
        barChart.setData(barData);
        barData.setBarWidth(.96f);
        /*Hides the legend because only displaying one measure*/
        Legend legend = barChart.getLegend();
        legend.setEnabled(false);

        /*Allows users to pitch to zoom*/
        barChart.setScaleEnabled(true);
        /*Gets the xAxis*/
        XAxis xAxis = barChart.getXAxis();
        /*Changes the numbers on the xAxis to days of the week
        * by getting passed into an axis formatter class*/
        String[] xLabels = {"", "Su", "M", "T", "W", "Th", "F", "Sa"};
        xAxis.setValueFormatter(new MyAxisFormatter(xLabels));
        /*Options selected -
        * draw the axis line and grid lines
        * set the minimum x value to zero
        * display the days of the week on the bottom of the graph
        **/
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMinimum(0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        /*Y-Axis options
        * Hide the right side of the axis
        * Set the minimum value to 0
        * */
        YAxis yLeftAxis = barChart.getAxisLeft();
        YAxis yRightAxis = barChart.getAxisRight();
        barChart.getAxisRight().setEnabled(false);
        yLeftAxis.setAxisMinimum(0f);
        /*Add a limit line to designated reached goals
        * Options:
        *  Change text color to navy
        *  Make text size 10
        *  Make line color navy
        * */
        LimitLine goal = new LimitLine(10f, "Goal Reached!");
        goal.setLineWidth(3f);
        goal.setTextColor(Color.rgb(4,50,124));
        goal.setTextSize(10f);
        goal.setLineColor(Color.rgb(4,50,124));
        yLeftAxis.addLimitLine(goal);

        /*Draw the axis line, and draw left gridlines*/
        yLeftAxis.setDrawAxisLine(true);
        yLeftAxis.setLabelCount(10, true);
        yLeftAxis.setDrawGridLines(true); //  grid lines
        //yRightAxis.setDrawGridLines(false); // no grid lines
        /*Take away description*/
        barChart.setDescription(null);
        /*since we are drawing the graph, call invalidate()*/
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
    /*This private class allows us to change the x-Axis labels to days
    * of the week instead of numbers*/
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
