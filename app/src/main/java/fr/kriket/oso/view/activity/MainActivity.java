package fr.kriket.oso.view.activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Random;

import fr.kriket.oso.R;
import fr.kriket.oso.controler.internal.GpsTrackerAlarmReceiver;
import fr.kriket.oso.service.LocationService;
import fr.kriket.oso.tools.SharedPreference;


// TODO: 1/9/17 remove session Id when stop
// TODO: 1/9/17 change track name for log name
// TODO: 1/9/17  check if location is ON
// TODO: 1/12/17 pause tracking
// TODO: 1/15/17 add interval setting



public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";


    ToggleButton toggle_log;
    Button bttn_mark_pt;

    private Boolean currentlyTracking;
    private int intervalInMinutes = 1;
    static  private AlarmManager alarmManager;
    private Intent gpsTrackerIntent;
    private PendingIntent pendingIntent;
    private SharedPreference sharedPreferences;


    final int LOCATION_PERMISSION_REQUEST_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = new SharedPreference();

        findViewsById();
       // About();
        //Format title bar
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("OSO");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Set listener
        toggle_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTrackclick(v);
            }
        });

        bttn_mark_pt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mark_Point();
            }
        });

        // check if alarm exist

        if (isalarmUp())
        {
            Log.d(TAG, "Alarm is already active");
            toggle_log.setChecked(true);
            showNotif();
        } else {
            Log.d(TAG, "Alarm is NOT active");
            toggle_log.setChecked(false);
            if (sharedPreferences.getValue(this,"SessionId") != null){
                // Remove Idsession
                sharedPreferences.removeValue(this,"SessionId");
            }
        }

    }

    public Boolean isalarmUp() {
        Intent alertIntent = new Intent(this, GpsTrackerAlarmReceiver.class);
        return (PendingIntent.getBroadcast(this, 100, alertIntent, PendingIntent.FLAG_NO_CREATE)!=null);

    }

    public Boolean checkLocationPermission(){
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }
    public void requestLocationPermission(){
        ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION }, LOCATION_PERMISSION_REQUEST_CODE);

    }


    public void toggleTrackclick(View v){

        if(toggle_log.isChecked()) {

            if (checkLocationPermission()) {

                startAlarmManager();
                Toast.makeText(MainActivity.this, "Start Tracking", Toast.LENGTH_SHORT).show();
            } else {
                requestLocationPermission();
            }

        } else {

            cancelAlarmManager();
            Toast.makeText(MainActivity.this, "End Tracking", Toast.LENGTH_SHORT).show();
        }
    }
    String extraComment=null;

    public void mark_Point() {
        Log.d(TAG, "markPoint");
        if (!isalarmUp()) {
            Log.d(TAG, "!isalarmUp()");
            sharedPreferences.save(this, "SessionId", generatedSessionId());
        }

        if (sharedPreferences.getValue(this, "SessionId") == null) {
            Log.d(TAG, "SessionId == null");
            sharedPreferences.save(this, "SessionId", generatedSessionId());
        }

        // remove extrra comment


        // Ask for comment
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompt_markpoint_comment, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Send",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text
                                if(userInput.getText().toString().length()>0){
                                    lauchMarkPointIntent(userInput.getText().toString());
                                }else{
                                    lauchMarkPointIntent(null);
                                }



                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }


    public void lauchMarkPointIntent(String mextracomment){
        // Created the intent
        Intent intentlocat=new Intent(this, LocationService.class);

        intentlocat.putExtra("isMarkPoint",true);
        intentlocat.putExtra("extraComment",mextracomment);
        this.startService(intentlocat);
    }






    int mNotifID=1;
    private void startAlarmManager() {

        Log.d(TAG, "startAlarmManager");

        // Store Idsession
        sharedPreferences.save(this,"SessionId",generatedSessionId());

        StartCancelRepeatingAlarm(this,true);
        // Set up the alarm

//        Context context = getBaseContext();
//        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        gpsTrackerIntent = new Intent(context, GpsTrackerAlarmReceiver.class);
//        pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, 0);
//
//        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//        SystemClock.elapsedRealtime(),
//        intervalInMinutes * 60000, // 60000 = 1 minute   // TODO: 1/5/17 Find a way to be down 60s
//        pendingIntent);

        showNotif();
    }

    private void cancelAlarmManager() {
        Log.d(TAG, "cancelAlarmManager");

        // Remove Idsession
        sharedPreferences.removeValue(this,"SessionId");

        StartCancelRepeatingAlarm(this,false);
        //Clear Alarm
//        Context context = getBaseContext();
//        Intent gpsTrackerIntent = new Intent(context, GpsTrackerAlarmReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//        alarmManager.cancel(pendingIntent);

        cancelNotif();
    }

    static void StartCancelRepeatingAlarm(Context context, boolean creating) {
        //if it already exists, then replace it with this one
        Intent alertIntent = new Intent(context, GpsTrackerAlarmReceiver.class);
        PendingIntent timerAlarmIntent = PendingIntent.getBroadcast(context, 100, alertIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (creating) {
            // Store Idsession

            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 1 * 60000, timerAlarmIntent);
        } else {
            timerAlarmIntent.cancel();
            alarmManager.cancel(timerAlarmIntent);
        }
    }

    private void showNotif() {
        // Display Notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notif_track_on)
                        .setContentTitle("OSO")
                        .setContentText("Logging is ON!")
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

    private void cancelNotif() {
        // Remove Notification
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(mNotifID);
    }



    private String generatedSessionId(){

     return  String.valueOf(rndChar())+
             String.valueOf(rndChar())+
             String.valueOf(randInt(0,9))+
             String.valueOf(randInt(0,9))+
             String.valueOf(randInt(0,9))+
             String.valueOf(rndChar())+
             String.valueOf(rndChar());

    }

    private static char rndChar () {
        int rnd = (int) (Math.random() * 52); // or use Random or whatever
        char base = (rnd < 26) ? 'A' : 'a';
        return (char) (base + rnd % 26);

    }

    /**
     * Returns a psuedo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.

     * @param min Minimim value
     * @param max Maximim value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int randInt(int min, int max) {

        // Usually this can be a field rather than a method variable
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive

        return rand.nextInt((max - min) + 1) + min;
    }

    /// Initialisation
    private void findViewsById() {


        //Create Toggle
        toggle_log = (ToggleButton) findViewById(R.id.toggleBtn_log);

        //Create button
        bttn_mark_pt = (Button) findViewById(R.id.bttn_mark_pt);
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
                Intent Start_TrackBook_Activite = new Intent(MainActivity.this, TrackBookActivity.class);
                startActivity(Start_TrackBook_Activite);
                return true;
            case R.id.main_2 :

                Intent Start_Preference_Activite = new Intent(MainActivity.this, preferenceActivity.class);
                startActivity(Start_Preference_Activite);

                return true;
            case R.id.main_3 :
                Intent Start_Debug_Activite = new Intent(MainActivity.this, DebugActivity.class);
                startActivity(Start_Debug_Activite);
                return true;
            case R.id.main_about :
                Intent Start_about_Activite = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(Start_about_Activite);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
