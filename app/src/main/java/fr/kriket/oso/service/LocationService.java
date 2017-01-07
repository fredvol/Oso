package fr.kriket.oso.service;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by fred on 1/3/17.
 */

public class LocationService extends Service implements LocationListener {

    private static final String TAG = "LocationService";

    private final Context mContext = this;


    // Flag for GPS status
    boolean isGPSEnabled = false;


    // Flag for GPS status
    boolean canGetLocation = false;

    Location location; // Location
    double latitude; // Latitude
    double longitude; // Longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;


    // Intent
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "------------------ \n startTracking" + " onStartCommand ");
        startTracking();
        return START_NOT_STICKY;
    }


    // START TRACKING
    private void startTracking() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ssZ");
        Log.d(TAG, "startTracking" + " Time: " + dateFormat.format(date));


        Location GpsPosition = getLocation();
        if (GpsPosition != null) {
            Log.d(TAG, " GPSTracking : " + GpsPosition.toString());
        } else {
            Log.d(TAG, " GPSTracking : NULL " );
        }


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "startTracking" + " onBind ");
        return null;
    }


    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // Getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!isGPSEnabled) {
                showSettingsAlert();
            } else {
                this.canGetLocation = true;

                // If GPS enabled, get latitude/longitude using GPS Services
                if (isGPSEnabled) {

                        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

//                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, this);
                        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);

                            if (locationManager != null) {
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            } else {
                                location=null;
                            }
                        }

                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }




    public void showSettingsAlert(){


        Log.d(TAG, "GPS NOT Enabled");
        Toast toast = Toast.makeText(mContext, "! GPS NOT AVAILABLE !", Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void onLocationChanged(Location location) {
      //  Log.d(TAG, "startTracking" + " onlocation changed "+ location.toString());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
