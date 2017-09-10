package com.acadgild.drawpathongooglemapexampleandroid;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import com.google.maps.android.SphericalUtil;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    private DBManager dbManager;
    private GoogleMap mMap;
    ArrayList<LatLng> MarkerPoints;
    LatLng current;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    double dist=0;
    double minimumDistance=50.00;
    TextView txt,distText,timeText,speedText;
    int track=0;
    long startTime ;
    long difference;
    String route_name="source-destination";
    String distance;
    int count=0;


    // ArrayList<LatLng> MarkerPoints1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Initializing
        MarkerPoints = new ArrayList<>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
       // txt=(TextView)findViewById(R.id.textView);
        distText=(TextView)findViewById(R.id.textView5);
        timeText=(TextView)findViewById(R.id.textView6);
        speedText=(TextView)findViewById(R.id.textView7);



        dbManager = new DBManager(this);
        dbManager.open();
        final Button startTracking = (Button) findViewById(R.id.button2);
        if(count==0) {
            if (startTracking != null) {
                startTracking.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        count++;
                        track = 1;
                        startTracking.setBackgroundColor(Color.BLACK);
                        startTracking.setTextColor(Color.WHITE);
                        startTracking.setClickable(false);
                        // Toast.makeText(MapsActivity.this,track,Toast.LENGTH_LONG).show();
                        //starting time  when start tracking is clicked
                        startTime = System.nanoTime();

                    }


                });
            }
        }

//reset map
         Button reset = (Button) findViewById(R.id.button8);
            if (reset != null) {
                reset.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = getIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        finish();
                        startActivity(intent);


                    }


                });
            }

            //stop tracking when button is pressed
        Button clickButton = (Button) findViewById(R.id.button3);
        if (clickButton != null) {
            clickButton.setOnClickListener( new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if(track==1 && dist>=minimumDistance) {
                        //customizing the text and background color
                        startTracking.setBackgroundColor(Color.LTGRAY);
                        startTracking.setTextColor(Color.BLACK);
                        count=0;
                        CameraPosition restoredCamera = new CameraPosition.Builder()
                                .target(new LatLng(current.latitude,current.longitude))
                                .zoom(18)
                                .bearing(0) // Face north
                                .tilt(5) // reset tilt (directly facing the Earth)
                                .build();

                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(restoredCamera));

                        track = 0;
                        //computing the time taken
                        difference = System.nanoTime() - startTime;

                        String[] a;
                        String b, c, d = "";
                        //Storing the longitudes and lattitudes in a single string by concatinting the markerPoints
                        StringBuilder listString = new StringBuilder();
                        for (int i = 1; i <= MarkerPoints.size(); i++) {
                            b = String.valueOf(MarkerPoints.get(i - 1).latitude);
                            c = String.valueOf(MarkerPoints.get(i - 1).longitude);
                            d = d.concat(b + "," + c);
                            //d = b + "," + c;
                            if (i <= MarkerPoints.size() - 1) {

                                d = d.concat("|");
                            }
                        }
                        String date = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
                        String time = new SimpleDateFormat("KK-mm  a").format(Calendar.getInstance().getTime());
                        //dbManager.insert(d);
                        int check = dbManager.insert(d,route_name,date,time, distance);
                        // Toast.makeText(getBaseContext(), d, Toast.LENGTH_LONG).show();
                        if (check != 1) {
                            Toast.makeText(getBaseContext(), "not working", Toast.LENGTH_LONG).show();
                        }

                        Intent main = new Intent(MapsActivity.this, SavedMap.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        main.putExtra("Uniqid",1);
                        finish();

                        startActivity(main);
                    }
                    else
                        Toast.makeText(getBaseContext(), "Tracking Not Started", Toast.LENGTH_LONG).show();
                }


            });
        }
    }


    public void recordLocation(Location loc) {
        LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
        String text= String.valueOf(latLng);
        MarkerPoints.add(latLng);
        draw();
        float zoomLevel = (float) 20.0; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
    }



    public double getDistance(LatLng src, LatLng dest) {

        LatLng from = new LatLng(src.latitude,src.longitude);
        LatLng to = new LatLng(dest.latitude,dest.longitude);

        //Calculating the distance in meters
        Double distance = SphericalUtil.computeDistanceBetween(from, to);

        //Displaying the distance
      //  Toast.makeText(this,String.valueOf(distance+" Meters"),Toast.LENGTH_SHORT).show();

        return distance;
    }


    public void draw() {

        SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        int pathColor = prefs.getInt("COLOR", Color.DKGRAY);

        if(MarkerPoints.size()>1) {
            //String str;
            float speed;

            int track = MarkerPoints.size() - 1;
            LatLng src = MarkerPoints.get(track-1);
            LatLng dest = MarkerPoints.get(track);

            PolylineOptions poly = new PolylineOptions().add(
                    new LatLng(src.latitude, src.longitude),
                    new LatLng(dest.latitude, dest.longitude)
            ).width(10).color(pathColor).geodesic(true);
            mMap.addPolyline(poly);


            difference = System.nanoTime() - startTime;

            DecimalFormat numberFormat = new DecimalFormat("#.00");
            dist=dist+getDistance(src,dest);
            distance=numberFormat.format(dist)+" Meters";

            speed= (float) (dist/TimeUnit.NANOSECONDS.toSeconds(difference)*3.6);

            distText.setText("Distance - "+numberFormat.format(dist)+" Meters");
            timeText.setText("Duration - "+  String.format("%d min, %d sec",
                    TimeUnit.NANOSECONDS.toMinutes(difference),
                    TimeUnit.NANOSECONDS.toSeconds(difference) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.NANOSECONDS.toMinutes(difference))));
            speedText.setText("Speed - "+(numberFormat.format(speed))+" km/h");

        }
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
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
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    //map will start drawing lines when location is changed when  minimumDistance=50.00
    @Override
    public void onLocationChanged(Location location) {

      if(track==1){
          current = new LatLng(location.getLatitude(), location.getLongitude());
          String text= String.valueOf(current);
          //Toast.makeText(MapsActivity.this,text,Toast.LENGTH_LONG).show();

          MarkerPoints.add(current);
          //move map camera
          draw();
          // mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
          float zoomLevel = (float) 20.0; //This goes up to 21
          mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, zoomLevel));


      }

else {

          if(mCurrLocationMarker != null){
              mCurrLocationMarker.remove();
          }
          MarkerOptions markerOptions = new MarkerOptions();
          current = new LatLng(location.getLatitude(), location.getLongitude());
          //String text= String.valueOf(latLng);
          //Toast.makeText(MapsActivity.this,text,Toast.LENGTH_LONG).show();

          // MarkerPoints.add(latLng);

          markerOptions.position(current);
          markerOptions.title("Current Position");
          markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
          mCurrLocationMarker = mMap.addMarker(markerOptions);
          float zoomLevel = (float) 20.0; //This goes up to 21
          mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, zoomLevel));


      }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

}

