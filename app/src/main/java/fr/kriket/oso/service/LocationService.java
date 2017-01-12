package fr.kriket.oso.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import fr.kriket.oso.controler.sqlite.DatabaseHandler;
import fr.kriket.oso.model.TrackPoint;
import fr.kriket.oso.tools.SharedPreference;

import static fr.kriket.oso.controler.sqlite.DatabaseHandler.*;


/**
 * Created by fred on 1/3/17.
 */

public class LocationService extends Service implements LocationListener{

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

    private SharedPreference sharedPreferences;





    // Intent
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPreferences = new SharedPreference();

        Log.d(TAG, "------------------ \n startTracking" + " onStartCommand ");
        Log.d(TAG,"SessionId: "+sharedPreferences.getValue(this,"SessionId"));
        startTracking();
        return START_NOT_STICKY;
    }


    // START TRACKING
    private void startTracking() {
        Date date = new Date();
        long mtimestamp = date.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss Z");
        Log.d(TAG, "startTracking" + " Time: " + dateFormat.format(date));

        Location GpsPosition = getLocation();
        if (GpsPosition != null) {
            TrackPoint trackPoint;
            trackPoint = new TrackPoint(sharedPreferences.getValue(this,"SessionId"),mtimestamp,date,GpsPosition.getLatitude(),GpsPosition.getLongitude(),GpsPosition.getAltitude(),GpsPosition.getAccuracy());
            trackPoint.setBat((int) getBatteryLevel());
            trackPoint.setNetworkStrength(getNetworkstrength());
            Log.d(TAG, " GPSTracking : " + trackPoint.toString());
            long rowinserted=add2DB(trackPoint);

            Log.d(TAG, " row added : " + rowinserted);

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
                showNoGPSAlert();
            } else {
                this.canGetLocation = true;

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
        catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }



    public void showNoGPSAlert(){
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

    public float getBatteryLevel() {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but I added just in case.
        if(level == -1 || scale == -1) {
            return 50.0f;
        }
        return ((float)level / (float)scale) * 100f;
    }

    public int getNetworkstrength(){
        TelephonyManager telephonyManager;
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);;


        int dBmlevel=0;

        List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();
        //Checking if list values are not null
        if (cellInfoList != null) {
            for (final CellInfo info : cellInfoList) {
                if (info instanceof CellInfoGsm) {
                    //GSM Network
                    CellSignalStrengthGsm cellSignalStrength = ((CellInfoGsm)info).getCellSignalStrength();
                    dBmlevel = cellSignalStrength.getDbm();
                }
                else if (info instanceof CellInfoCdma) {
                    //CDMA Network
                    CellSignalStrengthCdma cellSignalStrength = ((CellInfoCdma)info).getCellSignalStrength();
                    dBmlevel = cellSignalStrength.getDbm();
                }
                else if (info instanceof CellInfoLte) {
                    //LTE Network
                    CellSignalStrengthLte cellSignalStrength = ((CellInfoLte)info).getCellSignalStrength();
                    dBmlevel = cellSignalStrength.getDbm();
                }
                else if  (info instanceof CellInfoWcdma) {
                    //WCDMA Network
                    CellSignalStrengthWcdma cellSignalStrength = ((CellInfoWcdma)info).getCellSignalStrength();
                    dBmlevel = cellSignalStrength.getDbm();
                }

            }
        }
        return dBmlevel;

    }

    public long add2DB(TrackPoint trackPoint){

        DatabaseHandler mDbHelper = new DatabaseHandler(this,TRACKPT_TABLE_NAME,null,1);


        final String TRACKPT_TABLE_NAME = "TrackPointTable";

        // Gets the data repository in write mode

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        Log.d(TAG,"Db Path"+this.getDatabasePath(mDbHelper.TRACKPT_TABLE_NAME));
// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(TRACKPT_SESSIONID, trackPoint.getSessionId());
        values.put(TRACKPT_TIMESTAMP, trackPoint.getTimeStamp());
        values.put(TRACKPT_LAT, trackPoint.getLati());
        values.put(TRACKPT_LONG, trackPoint.getLong());
        values.put(TRACKPT_ALT, trackPoint.getAlt());
        values.put(TRACKPT_ACC, trackPoint.getAcc());
        values.put(TRACKPT_BAT, trackPoint.getBat());
        values.put(TRACKPT_NETWORKSTRENGH, trackPoint.getNetworkStrength());
        values.put(TRACKPT_COMMENT, trackPoint.getComment());
        values.put(TRACKPT_ISSENT, trackPoint.isSent());

// Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TRACKPT_TABLE_NAME, null, values);


        return newRowId;
    }
}
