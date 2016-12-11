package com.example.umiacs.hellohydration;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
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

public class BarGraph extends AppCompatActivity /*implements OnChartValueSelectedListener*/ {
    BarChart barChart;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bargraph);
        findViewById(R.id.bargraph).setBackgroundColor(Color.parseColor("#E0FCFC"));

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
        barChart.setTouchEnabled(true);
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
        //YAxis yRightAxis = barChart.getAxisRight();
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
        //set a listerned if bars are clicked
        //barChart.setOnChartGestureListener(this);
        barChart.setHighlightPerTapEnabled(true);
        barChart.setClickable(true);
        barChart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //if(barChart.va)
                Log.d("TAG", "TOUCHED IN : " + v.getId());
                setContentView(R.layout.activity_exercisetracker);
               // Intent activityTracker = new Intent(BarGraph.this, ExerciseTracker.class);
                //startActivity(activityTracker);
                TextView bikingTime = (TextView) findViewById(R.id.bikingTime);
                TextView walkingTime = (TextView) findViewById(R.id.walkingTime);
                TextView runningTime = (TextView) findViewById(R.id.runningTime);
                TextView date = (TextView) findViewById(R.id.currentDate);


                date.setText("December 4th, 2016");
                runningTime.setText("00:00");
                bikingTime.setText("00:15");
                walkingTime.setText("01:00");
            }
        });

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
   /* public Action getIndexApiAction() {
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

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        //TODO: Make tapping bar take you to activity page FOR THAT DAY
        SharedPreferences exerciseTracker = getSharedPreferences("exerciseTracker", Context.MODE_PRIVATE);
        /*long walking = exerciseTracker.getInt("minutesWalking", 0);
        long running = exerciseTracker.getInt("minutesRunning", 0);
        long biking = exerciseTracker.getInt("minutesBiking", 0);*/
      /*  setContentView(R.layout.activity_exercisetracker);
        Intent activityTracker = new Intent(BarGraph.this, ExerciseTracker.class);
        startActivity(activityTracker);
        TextView bikingTime = (TextView) findViewById(R.id.bikingTime);
        TextView walkingTime = (TextView) findViewById(R.id.walkingTime);
        TextView runningTime = (TextView) findViewById(R.id.runningTime);
        TextView date = (TextView) findViewById(R.id.currentDate);

     //   Log.d("MEEEE", "BT "+bikingTime + " WT " +walkingTime);
        //System.out.println(bikingTime);
       /* int walking = activityTracker.getIntExtra("minutesWalking",0);
        int running = activityTracker.getIntExtra("minutesRunning",0);
        int biking = activityTracker.getIntExtra("minutesBiking",0);
        //date.setText("December 4th, 2016");
         // walkingTime.setText("05:35");
  //      bikingTime.setText(Integer.toString(biking));
   //     runningTime.setText(Integer.toString(running));


    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }*/

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
