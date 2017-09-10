package com.acadgild.drawpathongooglemapexampleandroid;

import android.*;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.ArrayList;


public class SavedMap extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap1;
    GoogleApiClient mGoogleApiClient;
    ArrayList<LatLng> MarkerPoints;
    String[] mStrings;
    String test,test1;
    private DBManager dbManager;
    Cursor cursor;
    LatLng c,d;
    String route,date,time,distance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_map);
        dbManager = new DBManager(this);
        dbManager.open();
        TextView route1=(TextView)findViewById(R.id.routeName) ;
        TextView date1=(TextView)findViewById(R.id.dateText) ;
        TextView time1=(TextView)findViewById(R.id.timeText) ;
        TextView distance1=(TextView)findViewById(R.id.distanceText) ;

        //cursor = dbManager.fetch_complete();

        Intent intent = getIntent();
        if(intent !=null)
        {
           int strdata = intent.getExtras().getInt("Uniqid");
            if(strdata==1)
            {   //Toast.makeText(SavedMap.this,"here", Toast.LENGTH_LONG).show();
                cursor = dbManager.fetch_complete();

            }
            if(strdata==2)
            {    int id = Integer.parseInt(intent.getStringExtra("id"));
               //Toast.makeText(SavedMap.this, id, Toast.LENGTH_LONG).show();
                cursor = dbManager.fetch_single(id);
            }

        }

        route = cursor.getString(cursor.getColumnIndex(DatabaseHelper._ID));
        date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATE));
        time = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TIME));
        distance = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DISTANCE));
        route1.setText(route);
        date1.setText(date);
        time1.setText(time);
        distance1.setText(distance);
        setupMap();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //setupMap();

    }

    private void setupMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap1 = googleMap;

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap1.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap1.setMyLocationEnabled(true);
        }

        SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        int pathColor = prefs.getInt("COLOR", Color.DKGRAY);

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        MarkerOptions options = new MarkerOptions();

        //spliting the string data received from database and processing it for drawing on map individual long lat points
            test = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LON_LAT));
            test1 = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LON_LAT));
        String[] output = test.split("\\|",-1);
        if(output.length>=1) {
            for (int z = 1; z < output.length; z++) {

                //Toast.makeText(SavedMap.this, "hello", Toast.LENGTH_LONG).show();

                String[] parts = output[z - 1].split(",", 2);
                String string1 = parts[0];
                String string2 = parts[1];

                double latitude = Double.parseDouble(string1);
                double longitude = Double.parseDouble(string2);
                c = new LatLng(latitude, longitude);
               // MarkerPoints.add(c);
                String[] parts1 = output[z].split(",", 2);
                String string3 = parts1[0];
                String string4 = parts1[1];
                double latitude1 = Double.parseDouble(string3);
                double longitude1 = Double.parseDouble(string4);
                d = new LatLng(latitude1, longitude1);
                //MarkerPoints.add(d);
                // Setting the position of the marker



                 // For the start location, the color of marker is GREEN and
                 // for the end location, the color of marker is RED.

                if (z ==1) {
                    options.position(c);
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    //Toast.makeText(SavedMap.this,"Source", Toast.LENGTH_LONG).show();
                    mMap1.addMarker(options);
                } else if (z==output.length-1) {
                    options.position(d);
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    mMap1.addMarker(options);
                }


              PolylineOptions poly = new PolylineOptions().add(c, d).width(10).color(pathColor).geodesic(true);
                mMap1.addPolyline(poly);

                mMap1.moveCamera(CameraUpdateFactory.newLatLngZoom(d, 20));

            }

            //Toast.makeText(SavedMap.this, test, Toast.LENGTH_LONG).show();

        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
