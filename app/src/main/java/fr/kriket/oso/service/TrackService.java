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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.kriket.oso.controler.sqlite.DatabaseHandler;
import fr.kriket.oso.loader.external.askTrackingIDLoader;
import fr.kriket.oso.loader.internal.GetTrackPointFromDBLoader;
import fr.kriket.oso.model.Track;
import fr.kriket.oso.model.TrackPoint;

import static fr.kriket.oso.controler.sqlite.DatabaseHandler.TRACKPT_ACC;
import static fr.kriket.oso.controler.sqlite.DatabaseHandler.TRACKPT_ALT;
import static fr.kriket.oso.controler.sqlite.DatabaseHandler.TRACKPT_BAT;
import static fr.kriket.oso.controler.sqlite.DatabaseHandler.TRACKPT_COMMENT;
import static fr.kriket.oso.controler.sqlite.DatabaseHandler.TRACKPT_ISSENT;
import static fr.kriket.oso.controler.sqlite.DatabaseHandler.TRACKPT_LAT;
import static fr.kriket.oso.controler.sqlite.DatabaseHandler.TRACKPT_LONG;
import static fr.kriket.oso.controler.sqlite.DatabaseHandler.TRACKPT_NETWORKSTRENGH;
import static fr.kriket.oso.controler.sqlite.DatabaseHandler.TRACKPT_SESSIONID;
import static fr.kriket.oso.controler.sqlite.DatabaseHandler.TRACKPT_TABLE_NAME;
import static fr.kriket.oso.controler.sqlite.DatabaseHandler.TRACKPT_TIMESTAMP;


/**
 * Created by fred on 1/3/17.
 */

public class TrackService extends Service implements GetTrackPointFromDBLoader.GetTrackPointFromDBLoaderListener{

    private static final String TAG = "TrackService";

    private final Context mContext = this;

    private SharedPreferences sharedPref ;


    // Intent
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        Log.d(TAG, "------------------ \n startTracking" + " onStartCommand ");

        startTracking();
        return START_NOT_STICKY;
    }



    // START TRACKING
    private void startTracking() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss Z");
        Log.d(TAG, "startTracking" + " Time: " + dateFormat.format(date));

        if(hasTrackingID()){
            Log.d(TAG, " trackingID OK: " + sharedPref.getString("trackingID",null));
            startProcedureSending();
        } else {
            Log.d(TAG, " trackingID NO");
            askTrackingIDLoader askTrackingIDLoader = new askTrackingIDLoader(this);
            askTrackingIDLoader.execute();

            if (hasTrackingID()){
                startProcedureSending();
            }


        }
    }

    public boolean hasTrackingID(){
        return  sharedPref.getString("trackingID",null) != null;
    }

    public void startProcedureSending(){  //maybe useless
        Log.d(TAG, " sendpoint");
        selectTrackpoint2send(sharedPref.getString("sessionID",null));
    }

    public void selectTrackpoint2send(String sessionId){

        List<TrackPoint> trackPoints= new ArrayList<>();
        Log.d(TAG, "selectTrackpoint2send for seesionId: "+ sessionId);
        GetTrackPointFromDBLoader loader = new GetTrackPointFromDBLoader(this, this);
        loader.execute(sessionId);
    }




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "startTracking" + " onBind ");
        return null;
    }


    @Override
    public void onGetTrackPointFromDBLoaderSucess(List s) {
        Log.d(TAG, "onGGetTrackPointFromDBLoaderSucess" + s);
    }

    @Override
    public void onGetTrackPointFromDBLoaderFailed(String s) {
        Log.d(TAG, "onGetTrackPointFromDBLoaderFailed" + s);

    }
}
