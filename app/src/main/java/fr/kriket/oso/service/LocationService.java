package fr.kriket.oso.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
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


    // Declaring a Location Manager
    protected LocationManager locationManager;


    private SharedPreferences sharedPref ;



    boolean  isMarkPoint;
    String extraComment;



    // Intent
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        // Initialize Extras:
        isMarkPoint =false;
        extraComment =null;
        if (intent.getExtras() != null) {
            Log.d(TAG, "onstartCommand" + "Has intent extras: " + intent.getExtras().toString());

            if (intent.getExtras().containsKey("isMarkPoint")) {
                isMarkPoint = intent.getExtras().getBoolean("isMarkPoint",false);
            }

            if (intent.getExtras().containsKey("extraComment")) {

                extraComment = intent.getExtras().getString("extraComment",null);
                Log.d(TAG, "onstartCommand" + "extraComment: " + extraComment);

            }
        }

        Log.d(TAG, "------------------ \n startLogging" + " onStartCommand ");
        Log.d(TAG,"SessionId: "+sharedPref.getString("sessionID",null));
        startLogging();
        return START_NOT_STICKY;
    }



    // START TRACKING
    private void startLogging() {

        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            // Getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!isGPSEnabled) {
                showNoGPSAlert(); // Network manager : http://stackoverflow.com/questions/3145089/what-is-the-simplest-and-most-robust-way-to-get-the-users-current-location-on-a/3145655#3145655
            } else {
                this.canGetLocation = true;

                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "startLogging" + " resquest location update ");
                    locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);  // request location update
                    // TODO: 1/30/17 check for passive position ( for battery)  ?
                }

            }
        }
        catch (Exception e) {

            e.printStackTrace();
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "startLogging" + " onBind ");
        return null;
    }



    public void showNoGPSAlert(){
        Log.d(TAG, "GPS NOT Enabled");
        Toast toast = Toast.makeText(mContext, "! GPS NOT AVAILABLE !", Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void onLocationChanged(Location location) {

        Date date = new Date();

        Log.d(TAG, "startTracking" + "+ onlocation changed time:"+ location.getTime() +" location :"+ location.toString());
        if (location != null) {
            TrackPoint trackPoint;
            trackPoint = new TrackPoint(sharedPref.getString("sessionID",null),location.getTime(),date,location.getLatitude(),location.getLongitude(),location.getAltitude(),location.getAccuracy());
            trackPoint.setBat((int) getBatteryLevel());
            trackPoint.setNetworkStrength(getNetworkstrength());
            trackPoint.setComment(extraComment);
            Log.d(TAG, " GPSTracking : " + trackPoint.toString());

            long rowinserted=add2DB(trackPoint);

            if(rowinserted>0){
                Log.d(TAG, " row added : " + rowinserted);
                if (isMarkPoint) {
                    Log.d(TAG, " Markpoint added" );

                    Toast toast = Toast.makeText(mContext, "Mark Point Added !", Toast.LENGTH_LONG);
                    toast.show();

                }
            } else {
                Log.d(TAG, " row NOT added : " + rowinserted);
            }

        } else {
            Log.d(TAG, " GPSTracking : NULL " );
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        showNoGPSAlert();
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

    public long add2DB(TrackPoint trackPoint) {
       // final  int VERSION = 2;
       // final String TRACKPT_TABLE_NAME = "TrackPointTable";

        DatabaseHandler mDbHelper = new DatabaseHandler(this, TRACKPT_TABLE_NAME, null, DATABASE_VERSION);


        // Gets the data repository in write mode

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //Log.d(TAG, "Db Path" + this.getDatabasePath(mDbHelper.TRACKPT_TABLE_NAME));
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

        if(newRowId<0){
            Toast toast = Toast.makeText(mContext, "! Problem  ! Point not store in DB.", Toast.LENGTH_LONG);
            toast.show();
        }

        db.close();
        return newRowId;
    }
}
