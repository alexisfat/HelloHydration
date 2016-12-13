package com.example.umiacs.hellohydration;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    ImageButton imageButton;

    ImageView drop;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    SharedPreferences progress;
    SharedPreferences exerciseTracker;
    int progress_percent;
    double goalDouble;
    int lastDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //determine if this is the first time launching and display instructions if so
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        if(prefs.getBoolean("firstTime",true)){
            AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
            alert.setMessage("Enter user information in order to calculate a goal. Then tap the droplet to simulate drinking.");
            alert.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            alert.show();
            editor = prefs.edit();
            editor.putBoolean("firstTime", false);
            editor.commit();
        }

        //sets up progress display
        //TODO: replace with real tracking
        settings = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        editor = settings.edit();
        final TextView progressText = (TextView) findViewById(R.id.progressText);

        // Get the Drawable custom_progressbar
        // set the drawable as progress drawable
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        progressBar.setProgress(0);
        progressBar.setScaleY(63f);
        progressBar.setScaleX(1.05f);
        /*
        final Handler h = new Handler();
        final int delay = 50; //milliseconds

        h.postDelayed(new Runnable(){
            public void run(){

                //do something
                progressBar.incrementProgressBy(1);
                h.postDelayed(this, delay);
            }
        }, delay);
        */

        drop = (ImageView) findViewById(R.id.imageView2);
        drop.setOnClickListener(new View.OnClickListener() {
            ProgressBarAnimation anim;

            @Override
            public void onClick(View view) {
                switch(progressBar.getProgress()){
                    case 0:
                        anim = new ProgressBarAnimation(progressBar, 0, 200);
                        break;
                    case 200:
                        anim = new ProgressBarAnimation(progressBar, 200, 400);
                        break;
                    case 400:
                        anim = new ProgressBarAnimation(progressBar, 400, 600);
                        break;
                    case 600:
                        anim = new ProgressBarAnimation(progressBar, 600, 800);
                        break;
                    case 800:
                        anim = new ProgressBarAnimation(progressBar, 800, 1000);
                        break;
                    case 1000:
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "Progress reset" + progressBar.getProgress(), Toast.LENGTH_SHORT).show();
                        break;
                }
                progress_percent = progressBar.getProgress()/10;
                anim.setDuration(600);
                progressBar.startAnimation(anim);
                progressText.setText("Progress: " + (progress_percent/100.0)*goalDouble + " fl oz.");
                editor.putInt("progress", progress_percent);
                editor.commit();
            }
        });


    }

    @Override
    protected void onResume(){
        super.onResume();

        checkDayChange();

        /*
            goal calculation:
            [weight] * 0.5oz + [minutes of exercise] * 12oz
            https://www.umsystem.edu/newscentral/totalrewards/2014/06/19/how-to-calculate-how-much-water-you-should-drink/
        */
        TextView goalNum = (TextView) findViewById(R.id.goalNum);
        settings = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String weightStr = settings.getString("weight","");
        exerciseTracker = getSharedPreferences("exerciseTracker", Context.MODE_PRIVATE);
        int mins = exerciseTracker.getInt("minutesWalking", 0) +
                exerciseTracker.getInt("minutesRunning", 0) +
                exerciseTracker.getInt("minutesBiking", 0);
        if(weightStr.length() > 0) {    //user entered a weight
            goalDouble = Double.parseDouble(weightStr) * 0.5 + mins * 12;
            goalNum.setText(Double.toString(goalDouble) + " fl oz.");
            goalNum.setTextColor(getResources().getColor(R.color.navyBlue));
            Toast.makeText(getApplicationContext(), "Goal updated!", Toast.LENGTH_SHORT).show();
        }  else {
            goalNum.setText("None set");
        }
    }

    //sets up toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //handles selected toolbar menu option
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case(R.id.action_settings):
                intent = new Intent(MainActivity.this, Settings.class);
                startActivity(intent);
                break;
            case(R.id.action_bargraph):
                intent = new Intent(MainActivity.this, BarGraph.class);
                startActivity(intent);
                break;
            case(R.id.action_exercisetracker):
                intent = new Intent(MainActivity.this, ExerciseTracker.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void checkDayChange() {
        int today = Calendar.getInstance().DAY_OF_YEAR;
        if(today != lastDay) {
            //TODO: set up preferences and write progress and day
        }
        lastDay = today;
    }
}
