package com.acadgild.drawpathongooglemapexampleandroid;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


public class Settings extends AppCompatActivity {

    private RadioGroup colors;
    private RadioButton radioBtn;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        colors = (RadioGroup) findViewById(R.id.color);
        //int selectedId = colors.getCheckedRadioButtonId();

//using shared preferences to change the color and load the selected color in radio button

        SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        if(prefs.contains("COLOR"))
        { int pathColor = prefs.getInt("COLOR",0);
            switch(pathColor) {
                case Color.RED:
                    radioBtn = (RadioButton) findViewById(R.id.RED);
                    radioBtn.setChecked(true);
                    break;
                case Color.GREEN:
                    radioBtn = (RadioButton) findViewById(R.id.GREEN);
                    radioBtn.setChecked(true);
                    break;
                case Color.BLUE:
                    radioBtn = (RadioButton) findViewById(R.id.BLUE);
                    radioBtn.setChecked(true);
                    break;
                case Color.GRAY:
                    radioBtn = (RadioButton) findViewById(R.id.GREY);
                    radioBtn.setChecked(true);
                    break;
                default:
                    radioBtn = (RadioButton) findViewById(R.id.RED);
                    radioBtn.setChecked(true);
                    break;

            }

        }


        colors.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                int selectedId = colors.getCheckedRadioButtonId();
                int setColor=0;
                switch(selectedId) {
                    case R.id.RED:
                        setColor = Color.RED;
                        break;
                    case R.id.GREEN:
                        setColor = Color.GREEN;
                        break;
                    case R.id.BLUE:
                        setColor = Color.BLUE;
                        break;
                    case R.id.GREY:
                        setColor = Color.GRAY;
                        break;
                }

                SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS", MODE_PRIVATE).edit();
                //editor.putString("COLOR", "Elena");
                editor.putInt("COLOR", setColor);
                editor.apply();
                SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
                if(prefs.contains("COLOR"))
                    Toast.makeText(Settings.this,"Color Changed", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(Settings.this,"Error", Toast.LENGTH_LONG).show();


            }
        });


    }
}
