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
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
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
import fr.kriket.oso.loader.external.sendTrackPointLoader;
import fr.kriket.oso.loader.internal.GetTrackPointFromDBLoader;
import fr.kriket.oso.model.Track;
import fr.kriket.oso.model.TrackPoint;
import fr.kriket.oso.view.activity.MainActivity;

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
import static fr.kriket.oso.controler.sqlite.DatabaseHandler.TRACKPT_TRACKINGID;


/**
 * Created by fred on 1/3/17.
 */

public class TrackService extends Service implements GetTrackPointFromDBLoader.GetTrackPointFromDBLoaderListener ,sendTrackPointLoader.sendTrackPointLoaderListener,askTrackingIDLoader.askTrackingIDLoaderListener {

    private static final String TAG = "TrackService";

    private final Context mContext = this;
    private SharedPreferences sharedPref ;


    // Intent
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String imposeTrackingID =null;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Log.d(TAG, "---------->>>>> \n startTracking" + " onStartCommand ");
        if (intent.hasExtra("imposeTrackingID")) {
            imposeTrackingID=intent.getExtras().getString("imposeTrackingID");//intent.getStringExtra("imposeTrackingID");

        }



        startTracking(imposeTrackingID);
        return START_NOT_STICKY;
    }



    // START TRACKING
    private void startTracking(String imposeSessionID) {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss Z");
        Log.d(TAG, "startTracking" + " Time: " + dateFormat.format(date));

            if (hasTrackingID()) {
                Log.d(TAG, " trackingID (seesionID) OK: " + sharedPref.getString("sessionID", null));
                Log.d(TAG, " real trackingID : " + sharedPref.getString("trackingID", null));

                if (imposeSessionID != null) {
                    startProcedureSending(imposeSessionID);
                } else {
                    startProcedureSending(sharedPref.getString("sessionID", ""));
                }

            } else {
                Log.d(TAG, " trackingID NO");
                askTrackingIDLoader askTrackingIDLoader = new askTrackingIDLoader(this, this);
                askTrackingIDLoader.execute();
            }

    }

    public boolean hasTrackingID(){
        return  sharedPref.getString("trackingID",null) != null;
    }

    public void startProcedureSending(String SessionID){  //maybe useless
        Log.d(TAG, " sendpoint");

            selectTrackpoint2send(SessionID);
    }

    public void selectTrackpoint2send(String sessionId){

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
        Log.d(TAG, "onGGetTrackPointFromDBLoaderSucess: " + s);

        if(s.size()>0){
            Log.d(TAG, "s.size()>0  Sending ...");

            // add trackingID before sending
            List<TrackPoint> trackPoints =s;

            for (TrackPoint trackPoint : trackPoints) {
               trackPoint.setTrackingId(sharedPref.getString("trackingID",null));
            }

            // send the trackpoints
            sendTrackPointLoader sendTrackPointLoader = new sendTrackPointLoader(this, this);
            sendTrackPointLoader.execute(trackPoints);
        } else {
            Log.d(TAG, "No points to send");
        }

    }

    @Override
    public void onGetTrackPointFromDBLoaderFailed(String s) {
        Log.d(TAG, "onGetTrackPointFromDBLoaderFailed" + s);

    }

    @Override
    public void onsendTrackPointSent(List<String> results) {
        Log.d(TAG, "onsendTrackPointSent:" + results);
        if(results.size()>0){
            updateIsSent2DB(results);
        }
    }

    @Override
    public void onsendTrackPointFailed() {
        Log.d(TAG, "onsendTrackPointFailed" );

    }

    public boolean updateIsSent2DB(List<String> listTimestamp) {
        final  int VERSION = 2;
        final String TRACKPT_TABLE_NAME = "TrackPointTable";

        DatabaseHandler mDbHelper = new DatabaseHandler(this, TRACKPT_TABLE_NAME, null, VERSION); // TODO: 2/5/17  call uniform method


        // Gets the data repository in write mode
        ContentValues cv = new ContentValues();
        cv.put(TRACKPT_ISSENT,1);
        cv.put(TRACKPT_TRACKINGID,sharedPref.getString("trackingID",null));

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        
        List<Long> modifiedRow= new ArrayList<>();

        for(String s : listTimestamp) {
            long modifRowId=db.update(TRACKPT_TABLE_NAME, cv,TRACKPT_TIMESTAMP+"="+s, null);

            if(modifRowId>0){
                modifiedRow.add(modifRowId);
            }
        }
        db.close();

        Log.d(TAG, "updateIsSent2DB" + modifiedRow);
        if(modifiedRow.size()==listTimestamp.size()){
                     return true;
        } else {
            Toast toast = Toast.makeText(mContext, "! Problem  ! Point not updated in DB.", Toast.LENGTH_LONG);
            toast.show();
            return false;
        }

    }

    static final public String GETID_RESULT = "fr.kriket.oso.view.activity.MainActivity.TRACKID_RECEIVED";


    @Override
    public void onGetTrackingIDSucess() {
        Log.d(TAG, " onGetTrackingIDSucess");
        Intent intent = new Intent(GETID_RESULT);
        this.sendBroadcast(intent);

        startProcedureSending(sharedPref.getString("trackingID",null));

    }

    @Override
    public void onGetTrackingIDFailed() {
        Log.d(TAG, " onGetTrackingIDFailed");
        Toast toast = Toast.makeText(mContext, "! No Tracking ID.", Toast.LENGTH_LONG);
        toast.show();

    }
}
