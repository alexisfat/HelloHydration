package com.example.umiacs.hellohydration;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;

/**
 * Created by alexandraboukhvalova on 12/3/16.
 */

public class ExerciseTracker extends AppCompatActivity implements SensorEventListener {
    //allow exercise info to be shared with whole application
    SharedPreferences exerciseTracker;

    //fields tracking user's exercise time
    //will be accessed by main activity to calculate how much water should be consumed
    public  int minutesWalking = 0;
    public int minutesRunning = 0;
    public int minutesBiking = 0;
    private Boolean tracking = false;

    private Handler mHandler = new Handler();
    private long startTime;
    private long elapsedTime;
    private String hours,minutes,seconds,milliseconds;
    private long secs,mins,hrs;

    //exercise activities enumerated
    final int notMoving = 0;
    final int walking = 1;
    final int running = 2;
    final int biking = 3;
    //exercise activity as recognized by accelerometer
    private int activity = 0;

    //exercise velocity limits in meters/second
    //walking max velocity calculated based on assumption that person can walk no faster than 4mph
    private double walkingMax = 1.78816;
    //running max velocity calculated based on assumption that person will run at a max speed of 9.6mph
    private double runningMax = 4.29;

    //Velocity as calculated from accelerometer and time elapsed
    private double velocity = 0;

    private double userInitialAcceleration;

    //Setting up fields for android accelerometer sensor
    private SensorManager sensorManager;
    private Sensor accelerometer;

    private long lastUpdate = 0;
    //Boolean to keep track of when the acceleration after 5 seconds has been recorded
    private boolean accTaken = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercisetracker);

        //setting up accelerometer sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);


        final Button trackingButton = (Button) findViewById(R.id.trackbutton);

        trackingButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (!tracking){
                    tracking = true;
                    trackingButton.setText("Stop Tracking");

                    //getting initial time to calculate elapsed exercise time
                    startTime = System.currentTimeMillis();
                } else {
                    trackingButton.setText("Start Tracking");

                    //timing activity
                    elapsedTime = System.currentTimeMillis() - startTime;

                    //calculating user's initial velocity based on acceleration in first 5 seconds
                    velocity = (userInitialAcceleration * 5);

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

                    updateTimer(elapsedTime, activity);

                    //share updated exercise times with the rest of the applications
                    SharedPreferences.Editor editor = exerciseTracker.edit();

                    //store inputs
                    editor.putLong("minutesWalking", minutesWalking);
                    editor.putLong("minutesRunning", minutesRunning);
                    editor.putLong("minutesBiking", minutesBiking);
                    editor.commit();

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
    private void updateTimer (float elapsedTime, int activity){
        float time = 0;

        //the time displayed will account for the elapsed time for the most recent exercise
        switch(activity){
            case 1:
                time = minutesWalking + elapsedTime;
                break;
            case 2:
                time = minutesRunning + elapsedTime;
                break;
            case 3:
                time = minutesBiking + elapsedTime;
                break;
        }

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
        if(milliseconds.length()<=1){
            milliseconds = "00";
        }
        milliseconds = milliseconds.substring(milliseconds.length()-3, milliseconds.length()-2);

        /* Setting the exercise times to include the elapsed time */
        switch(activity){
            case 0:
                break;
            case 1:
                ((TextView)findViewById(R.id.walkingTime)).setText(hours + ":" + minutes + ":" + seconds);
                break;
            case 2:
                ((TextView)findViewById(R.id.runningTime)).setText(hours + ":" + minutes + ":" + seconds);
                break;
            case 3:
                ((TextView)findViewById(R.id.bikingTime)).setText(hours + ":" + minutes + ":" + seconds);
                break;
        }

    }

    //Sensor code based on Android developer page
    //https://developer.android.com/reference/android/hardware/SensorEvent.html#values
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        float[] gravity = new float[3];
        float[] linear_acceleration = new float[3];

        if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

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
            }
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
