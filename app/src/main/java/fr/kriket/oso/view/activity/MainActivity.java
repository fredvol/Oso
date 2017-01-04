package fr.kriket.oso.view.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        // Shared Pref

        currentlyTracking = sharedPreferences.getBoolean(this,"currentlyTracking");


        //Start Mouv Visu

        toggle_track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTrackclick(v);
            }
        });

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

    private void startAlarmManager() {
        Log.d(TAG, "startAlarmManager");
        Context context = getBaseContext();
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        gpsTrackerIntent = new Intent(context, GpsTrackerAlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, 0);

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
        SystemClock.elapsedRealtime(),
        intervalInMinutes * 60000, // 60000 = 1 minute
        pendingIntent);
    }

    private void cancelAlarmManager() {
        Log.d(TAG, "cancelAlarmManager");

        Context context = getBaseContext();
        Intent gpsTrackerIntent = new Intent(context, GpsTrackerAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, 0);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
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
