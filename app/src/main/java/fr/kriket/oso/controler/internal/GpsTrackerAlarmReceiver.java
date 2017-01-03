package fr.kriket.oso.controler.internal;

/**
 * Created by fred on 1/3/17.
 */
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import fr.kriket.oso.service.LocationService;

// make sure we use a WakefulBroadcastReceiver so that we acquire a partial wakelock
public class GpsTrackerAlarmReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = "GpsTrackerAlarmReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "received ");
        context.startService(new Intent(context, LocationService.class));
    }
}

