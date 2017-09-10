package com.acadgild.drawpathongooglemapexampleandroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class Login extends AppCompatActivity {

    private DBManager dbManager;
    protected LocationManager mLocationManager;
    private static final int GPS_ENABLE_REQUEST = 0x1001;
    private AlertDialog mGPSDialog;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        dbManager = new DBManager(this);
        dbManager.open();
        Button openMap = (Button) findViewById(R.id.button4);

        //checking if gps is enabled else dialog will be shown
        if (openMap != null) {
            openMap.setOnClickListener( new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
                    boolean enabled = service
                            .isProviderEnabled(LocationManager.GPS_PROVIDER);

                    if (!enabled) {
                        showDialogGPS();
                    }
                    else {
                        Intent main = new Intent(Login.this, MapsActivity.class);
                        //finish();
                        startActivity(main);
                    }
                }


            });
        }

        Button openSavedMap = (Button) findViewById(R.id.button5);
        if (openSavedMap != null) {
            openSavedMap.setOnClickListener( new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent main = new Intent(Login.this, SavedMapsList.class);
                    //finish();
                    startActivity(main);
                }


            });
        }

        Button openSettings = (Button) findViewById(R.id.button6);
        if (openSettings != null) {
            openSettings.setOnClickListener( new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent main = new Intent(Login.this, Settings.class);
                    startActivity(main);
                }


            });
        }
    }

    // dialog will be shown when gps is not enabled
    private void showDialogGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("GPS Disabled");
        builder.setMessage("Gps is disabled, in order to use the application properly you need to enable GPS of your device");
        builder.setPositiveButton("Enable GPS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), GPS_ENABLE_REQUEST);
            }
        }).setNegativeButton("No, Just Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        mGPSDialog = builder.create();
        mGPSDialog.show();
    }

    //if user enabled the gps and returns, the MapsActivity will start
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        //Toast.makeText(Login.this,"Enabled", Toast.LENGTH_LONG).show();
        if (enabled) {
            Intent main = new Intent(Login.this, MapsActivity.class);
            //finish();
            startActivity(main);
        }



    }
}
