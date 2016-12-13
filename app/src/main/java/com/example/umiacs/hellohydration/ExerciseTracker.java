package com.example.umiacs.hellohydration;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Timer;

/**
 * Created by alexandraboukhvalova on 12/3/16.
 * Location tracking code referenced from:
 * https://developer.android.com/guide/topics/location/strategies.html
 */

public class ExerciseTracker extends AppCompatActivity implements SensorEventListener {
    //allow exercise info to be shared with whole application
    SharedPreferences exerciseTracker;

    //fields tracking user's exercise time
    //will be accessed by main activity to calculate how much water should be consumed
    public int secondsWalking = 0;
    public  int minutesWalking = 0;
    public int hoursWalking = 0;
    public int secondsRunning = 0;
    public int minutesRunning = 0;
    public int hoursRunning = 0;
    public int secondsBiking = 0;
    public int minutesBiking = 0;
    public int hoursBiking = 0;
    private Boolean tracking = false;

    private TextView tempTextView; //Temporary TextView
    private Button tempBtn; //Temporary Button

    private long _lastTimeAccelSenorChangeInMs = -1;

    private Handler mHandler = new Handler();
    private long startTime;
    private long elapsedTime;
    private final int REFRESH_RATE = 1000;
    private String hours,minutes,seconds,milliseconds;
    private long secs,mins,hrs, msecs;
    private boolean stopped = false;

    //exercise activities enumerated
    final int notMoving = 0;
    final int walking = 1;
    final int running = 2;
    final int biking = 3;
    //exercise activity as recognized by accelerometer
    private int activity = 0;

    //exercise velocity limits in meters/second
    //walking max velocity calculated based on assumption that person can walk no faster than 4.5mph
    private double walkingMax = 2;
    //running max velocity calculated based on assumption that person will run at a max speed of 9.6mph
    private double runningMax = 4.29;

    //Velocity as calculated from accelerometer and time elapsed
    private double velocity = 0;

    private double userInitialAcceleration = -1;

    //Setting up fields for android accelerometer sensor
    private SensorManager sensorManager;
    private Sensor accelerometer;

    private long lastUpdate = 0;
    //Boolean to keep track of when the acceleration after 5 seconds has been recorded
    private boolean accTaken = false;

    long distanceTravelled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercisetracker);


        //setting up accelerometer sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        final ImageButton trackingButton = (ImageButton) findViewById(R.id.trackbutton);

        trackingButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (!tracking){
                    tracking = true;
                    trackingButton.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
                    ((TextView)findViewById(R.id.timer)).setVisibility(View.VISIBLE);
                            //setText("Stop Tracking");

                    //getting initial time to calculate elapsed exercise time
                    startTime = System.currentTimeMillis();

                    mHandler.removeCallbacks(startTimer);
                    mHandler.postDelayed(startTimer, 0);
                } else {
                    trackingButton.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);

                    //timing activity
                    //elapsedTime = System.currentTimeMillis() - startTime;

                    //calculating user's initial velocity based on acceleration in first 5 seconds
                    velocity = (userInitialAcceleration * 5);

                    /*distanceTravelled = calculateDistance(longitudeStart, latitudeStart,
                            longitudeStop, latitudeStop);
                    velocity = distanceTravelled / elapsedTime;*/

                    if(velocity >= 0){
                        activity = walking;
                    }
                    //setting exercise activity based on velocity
                    if (velocity < walkingMax) {
                        //person is walking
                        activity = walking;
                    } else if (walkingMax < velocity && velocity < runningMax) {
                        //person is running
                        activity = running;
                    } else if (velocity > runningMax) {
                        //person is biking
                        activity = biking;
                    } else if (velocity == 0) {
                        activity = notMoving;
                    }

                    updateActivityTimer(activity);
                    /*longitudeStop = location.getLongitude();
                    latitudeStop = location.getLatitude();*/


                    //share updated exercise times with the rest of the applications
                    SharedPreferences.Editor editor = exerciseTracker.edit();

                    //store inputs
                    editor.putInt("minutesWalking", minutesWalking);
                    editor.putInt("minutesRunning", minutesRunning);
                    editor.putInt("minutesBiking", minutesBiking);
                    editor.commit();

                    ((TextView)findViewById(R.id.timer)).setVisibility(View.GONE);
                    ((TextView)findViewById(R.id.timer)).setText("00:00:00");

                    //reset booleans until next time user starts tracking again
                    tracking = false;
                    accTaken = false;
                }
            }
        });

        exerciseTracker = getSharedPreferences("exerciseTracker", Context.MODE_PRIVATE);

    }

    //Majority of method code obtained from
    //http://www.shawnbe.com/index.php/tutorial/tutorial-3-a-simple-stopwatch-lets-add-the-code/
    private void updateTimer (float time){


        secs = (long)(time/1000);
        mins = (long)((time/1000)/60);
        hrs = (long)(((time/1000)/60)/60);

		/* Convert the seconds to String
		 * and format to ensure it has
		 * a leading zero when required
		 */
        secs = secs % 60;
        seconds=String.valueOf(secs);
        if(secs == 0){
            seconds = "00";
        }
        if(secs <10 && secs > 0){
            seconds = "0"+seconds;
        }

		/* Convert the minutes to String and format the String */

        mins = mins % 60;
        minutes=String.valueOf(mins);
        if(mins == 0){
            minutes = "00";
        }
        if(mins <10 && mins > 0){
            minutes = "0"+minutes;
        }

    	/* Convert the hours to String and format the String */

        hours=String.valueOf(hrs);
        if(hrs == 0){
            hours = "00";
        }
        if(hrs <10 && hrs > 0){
            hours = "0"+hours;
        }

    	/* Although we are not using milliseconds on the timer in this example
    	 * I included the code in the event that you wanted to include it on your own
    	 */
        milliseconds = String.valueOf((long)time);
        if(milliseconds.length()==2){
            milliseconds = "0"+milliseconds;
        }

        //if(milliseconds.length()<=1){
        //    milliseconds = "00";
        //}
        //milliseconds = milliseconds.substring(milliseconds.length()-2, milliseconds.length()-1);

        /* Setting the timer text to the elapsed time */
        ((TextView)findViewById(R.id.timer)).setText(hours + ":" + minutes + ":" + seconds);
        //((TextView)findViewById(R.id.timerMs)).setText("." + milliseconds);

    }

    private void updateActivityTimer (int activity){

        secs = (elapsedTime/1000);
        long totalSeconds = secs;
        mins = ((elapsedTime/1000)/60);

        hrs = (((elapsedTime/1000)/60)/60);

        String updateMins = "";
        String updateSec = "";
        String updateHr = "";

        /* Setting the exercise times to include the elapsed time */
        switch(activity){

            case 0:
                break;
            case 1:
                secondsWalking += totalSeconds;
                minutesWalking += (secondsWalking/60);
                hoursWalking += hrs;

                updateMins = getCombinedMinutes(minutesWalking);
                updateSec = getCombinedSeconds(secondsWalking);
                updateHr = getCombinedSeconds(hoursWalking);

                ((TextView)findViewById(R.id.walkingTime)).setText(updateHr + ":" + updateMins + ":" + updateSec);
                break;
            case 2:
                secondsRunning += totalSeconds;
                minutesRunning += (secondsRunning/60);
                hoursBiking += hrs;

                updateMins = getCombinedMinutes(minutesRunning);
                updateSec = getCombinedSeconds(secondsRunning);
                updateHr = getCombinedSeconds(hoursWalking);

                ((TextView)findViewById(R.id.runningTime)).setText(updateHr + ":" + updateMins + ":" + updateSec);
                break;
            case 3:
                secondsBiking += totalSeconds;
                minutesBiking += (secondsBiking/60);
                hoursBiking += hrs;

                updateMins = getCombinedMinutes(minutesBiking);
                updateSec = getCombinedSeconds(secondsBiking);
                updateHr = getCombinedSeconds(hoursWalking);

                ((TextView)findViewById(R.id.bikingTime)).setText(updateHr + ":" + updateMins + ":" + updateSec);
                break;
        }
    }

    private String getCombinedSeconds(long existingActivitySeconds) {
        long totalSecs = (existingActivitySeconds) % 60;
        seconds=String.valueOf(totalSecs);
        if(totalSecs == 0){
            seconds = "00";
        }
        if(totalSecs <10 && totalSecs > 0){
            seconds = "0"+seconds;
        }

        return seconds;
    }

    private String getCombinedMinutes(long existingActivityMinutes) {
        /* Convert the minutes to String and format the String */

        long totalMins = (existingActivityMinutes) % 60;

        minutes=String.valueOf(totalMins);
        if(totalMins == 0){
            minutes = "00";
        }
        if(totalMins <10 && totalMins > 0){
            minutes = "0"+minutes;
        }

        return minutes;
    }

    private String getCombinedHours(long existingActivityHours) {

        hours=String.valueOf(existingActivityHours);
        if(existingActivityHours == 0){
            hours = "00";
        }
        if(existingActivityHours <10 && existingActivityHours > 0){
            hours = "0"+hours;
        }

        return hours;
    }

    private Runnable startTimer = new Runnable() {
        public void run() {
            elapsedTime = System.currentTimeMillis() - startTime;
            updateTimer(elapsedTime);
            mHandler.postDelayed(this,REFRESH_RATE);
        }
    };

    /* To calculate distance between two points.
    * Code obtained from:
    * http://stackoverflow.com/questions/20398898/how-to-get-speed-in-android-app-using-location
    * -or-accelerometer-or-some-other-wa*/
    private static long calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        long distanceInMeters = Math.round(6371000 * c);
        return distanceInMeters;
    }

    //Sensor code based on Android developer page
    //https://developer.android.com/reference/android/hardware/SensorEvent.html#values
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        float[] gravity = new float[3];
        float[] linear_acceleration = new float[3];

       switch(sensorEvent.sensor.getType()){
           case Sensor.TYPE_ACCELEROMETER:
                if(_lastTimeAccelSenorChangeInMs == -1){
                    _lastTimeAccelSenorChangeInMs = SystemClock.elapsedRealtime();
                }
            //alpha is calculated as t/ (t + dT) as taken from Android documentation
            final float alpha = 0.8f;

               long curTime = System.currentTimeMillis();

            if (((curTime - lastUpdate) > 5000) && !accTaken && tracking) {
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];

                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                gravity[0] = alpha * (1 - alpha) * x;
                gravity[1] = alpha * (1 - alpha) * y;
                gravity[2] = alpha * (1 - alpha) * z;

                linear_acceleration[0] = Math.abs(x - gravity[0]);
                linear_acceleration[1] = Math.abs(y - gravity[1]);
                linear_acceleration[2] = Math.abs(z - gravity[2]);

                //assuming initial acceleration is the maximum of the x,y,z accelerations
                //Accounting for various ways the user will hold their phone in space

                //for comparing max linear acceleration
                float temp = 0;
                temp = Math.max(linear_acceleration[0], linear_acceleration[1]);
                userInitialAcceleration = Math.max(temp, linear_acceleration[2]);

                accTaken = true;

                Log.d("X", Float.toString(linear_acceleration[0]));
                Log.d("Y", Float.toString(linear_acceleration[1]));
                Log.d("Z", Float.toString(linear_acceleration[2]));

            }
               _lastTimeAccelSenorChangeInMs = curTime;

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

}
