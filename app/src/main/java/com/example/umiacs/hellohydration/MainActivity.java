package com.example.umiacs.hellohydration;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    ImageButton imageButton;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    int progress;

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
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(progress){
                    case 0:
                        imageButton.setImageResource(R.drawable.drop25);
                        progress = 25;
                        break;
                    case 25:
                        imageButton.setImageResource(R.drawable.drop50);
                        progress = 50;
                        break;
                    case 50:
                        imageButton.setImageResource(R.drawable.drop75);
                        progress = 75;
                        break;
                    case 75:
                        imageButton.setImageResource(R.drawable.drop100);
                        progress = 100;
                        break;
                    case 100:
                        imageButton.setImageResource(R.drawable.drop0);
                        progress = 0;
                        Toast.makeText(getApplicationContext(), "Progress reset", Toast.LENGTH_SHORT).show();
                        break;
                }
                progressText.setText("Progress: " + (progress/100.0)*64 + " fl oz.");
                editor.putInt("progress", progress);
                editor.commit();
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();

        //calculate goal from settings
        //TODO: actually calculate goal from settings
        settings = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        if(settings.getString("name","").length() > 0) {    //user entered at least a name in settings
            TextView goalNum = (TextView) findViewById(R.id.goalNum);
            goalNum.setText("64 fl oz.");
            Toast.makeText(getApplicationContext(), "Goal updated!", Toast.LENGTH_SHORT).show();
        }
    }

    //sets up toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
