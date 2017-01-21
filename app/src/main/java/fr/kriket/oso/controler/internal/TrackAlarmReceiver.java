package fr.kriket.oso.controler.internal;

/**
 * Created by fred on 1/3/17.
 */
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import fr.kriket.oso.service.LocationService;
import fr.kriket.oso.service.TrackService;
import fr.kriket.oso.view.activity.MainActivity;


public class TrackAlarmReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = "TrackAlarmReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"trackreceiver");
        context.startService(new Intent(context, TrackService.class));
    }
}

