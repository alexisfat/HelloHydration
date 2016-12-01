package com.example.umiacs.hellohydration;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class Settings extends AppCompatActivity {
    SharedPreferences prefs;
    String name, age, sex, ft, in, weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //set up entry fields
        final EditText nameField = (EditText) findViewById(R.id.nameField);
        final EditText ageField = (EditText) findViewById(R.id.ageField);
        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        final EditText ftField = (EditText) findViewById(R.id.ftField);
        final EditText inField = (EditText) findViewById(R.id.inField);
        final EditText weightField = (EditText) findViewById(R.id.weightField);

        //set up save button
        //TODO: make sure input is valid
        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = prefs.edit();

                //get input values
                name = nameField.getText().toString();
                age = ageField.getText().toString();
                RadioButton checked = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
                sex = checked.getText().toString();
                ft = ftField.getText().toString();
                in = inField.getText().toString();
                weight = weightField.getText().toString();

                //store inputs
                editor.putString("name", name);
                editor.putString("age", age);
                editor.putString("sex", sex);
                editor.putString("ft", ft);
                editor.putString("in", in);
                editor.putString("weight", weight);
                editor.commit();

                Toast.makeText(getApplicationContext(), "Settings saved!", Toast.LENGTH_SHORT).show();
            }
        });

        //load saved entries, set empty if not present
        prefs = getPreferences(Context.MODE_PRIVATE);
        name = prefs.getString("name","");
        age = prefs.getString("age","");
        sex = prefs.getString("sex","");
        ft = prefs.getString("ft","");
        in = prefs.getString("in","");
        weight = prefs.getString("weight","");

        //put saved entries into entry fields
        nameField.setText(name);
        ageField.setText(age);
        RadioButton radio;
        if(sex.equals("Female")) {
            radio = (RadioButton) findViewById(R.id.femaleButton);
            radio.setChecked(true);
        } else if(sex.equals("Male")){
            radio = (RadioButton) findViewById(R.id.maleButton);
            radio.setChecked(true);
        }
        ftField.setText(ft);
        inField.setText(in);
        weightField.setText(weight);
    }

}
