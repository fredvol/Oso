package fr.kriket.oso.view.activity;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import fr.kriket.oso.BuildConfig;
import fr.kriket.oso.R;
import fr.kriket.oso.controler.internal.GpsTrackerAlarmReceiver;
import fr.kriket.oso.tools.SharedPreference;


// TODO: 1/5/17 Button display  if tracking or not onload 


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";


    TextView txtview_about;
    TextView toggle_track;

    private Boolean currentlyTracking;
    private int intervalInMinutes = 1;
    private AlarmManager alarmManager;
    private Intent gpsTrackerIntent;
    private PendingIntent pendingIntent;
    private SharedPreference sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = new SharedPreference();

        findViewsById();
        About();
        //Format title bar
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("OSO CORE");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Set listener
        toggle_track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTrackclick(v);
            }
        });
        // Shared Pref

        boolean currentlyTracking = sharedPreferences.getBoolean(this,"currentlyTracking"); // USELESS?

        // check if alarm exist
        boolean alarmUp = (PendingIntent.getBroadcast(this, 0,
                new Intent(this, GpsTrackerAlarmReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null);

        if (alarmUp)
        {
            Log.d(TAG, "Alarm is already active");
        } else {
            Log.d(TAG, "Alarm is NOT active");
        }




    }

    public void toggleTrackclick(View v){



        if(toggle_track.getText().equals("ON")) {

            startAlarmManager();

            currentlyTracking = true;
            Toast.makeText(MainActivity.this, "Start Tracking", Toast.LENGTH_SHORT).show();
            sharedPreferences.save(this,"currentlyTracking",true);

        } else {

            cancelAlarmManager();
            Toast.makeText(MainActivity.this, "End Tracking", Toast.LENGTH_SHORT).show();
            currentlyTracking = false;
            sharedPreferences.save(this,"currentlyTracking",false);
        }


    }



    int mNotifID=1;
    private void startAlarmManager() {

        Log.d(TAG, "startAlarmManager");

        // Set up the alarm
        Context context = getBaseContext();
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        gpsTrackerIntent = new Intent(context, GpsTrackerAlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, 0);

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
        SystemClock.elapsedRealtime(),
        intervalInMinutes * 60000, // 60000 = 1 minute   // TODO: 1/5/17 Find a way to be down 60s
        pendingIntent);


        // Display Notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notif_track_on)
                        .setContentTitle("OSO")
                        .setContentText("Tracking is ON!")
                        .setAutoCancel(false)
                        .setOngoing(true);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT); //Intent to open main activity when click on Notification

        mBuilder.setContentIntent(contentIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mNotifID allows you to update the notification later on.
        mNotificationManager.notify(mNotifID, mBuilder.build());


    }

    private void cancelAlarmManager() {
        Log.d(TAG, "cancelAlarmManager");

        //Clear Alarm
        Context context = getBaseContext();
        Intent gpsTrackerIntent = new Intent(context, GpsTrackerAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, 0);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        // Remove Notification
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(mNotifID);

    }

    private void About() {
        // Update about text
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

            String version = pInfo.versionName;
            int verCode = pInfo.versionCode;

            String strabout2="Version app: "+ version + "   Version code: "+ verCode +'\n' +"Build Variant : "+ BuildConfig.BUILD_TYPE;

            txtview_about.setText(strabout2);


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    /// Initialisation
    private void findViewsById() {

        //Create Textview
        txtview_about = (TextView) findViewById(R.id.txtView_about);

        //Create Toggle
        toggle_track = (ToggleButton) findViewById(R.id.toggleBtn_track);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
                return true;
            case R.id.main_1 :
                Toast.makeText(this, "mainMenu_1", Toast.LENGTH_LONG).show();
                return true;
            case R.id.main_2 :
                Toast.makeText(this, "mainMenu_2 setting", Toast.LENGTH_LONG).show();
                return true;
            case R.id.main_3 :
                Intent Start_Debug_Activite = new Intent(MainActivity.this, DebugActivity.class);
                startActivity(Start_Debug_Activite);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
