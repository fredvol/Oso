package fr.kriket.oso.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by fred on 1/3/17.
 */

public class LocationService extends Service {

    private static final String TAG = "LocationService";


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


            startTracking();


        return START_NOT_STICKY;
    }

    private void startTracking() {
        Date date = new Date();
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        Log.d(TAG, "startTracking" +" Time: " + dateFormat.format(date));

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
