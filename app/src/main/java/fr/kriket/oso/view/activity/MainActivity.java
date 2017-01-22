package fr.kriket.oso.view.activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
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
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Random;

import fr.kriket.oso.R;
import fr.kriket.oso.controler.internal.GpsTrackerAlarmReceiver;
import fr.kriket.oso.controler.internal.TrackAlarmReceiver;
import fr.kriket.oso.service.LocationService;
import fr.kriket.oso.service.TrackService;


// TODO: 1/21/17 set in the manifest all view as portrait

// TODO: 1/9/17  check if location is ON
// TODO: 1/12/17 pause tracking




public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";


    ToggleButton toggle_log;
    ToggleButton toggle_track;
    Button bttn_mark_pt;
    Button bttn_send_pt;
    EditText editText_track_link;
    ImageButton imageBttn_share;


    final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private SharedPreferences sharedPref ;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Other share pref
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //editor
        editor = sharedPref.edit();

        //Initialise all variable if first start
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

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
                toggleLogclick(v);
            }
        });

        toggle_track.setOnClickListener(new View.OnClickListener() {
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

        bttn_send_pt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_Point();

            }
        });

        imageBttn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharetrackID();
                  }
        });





        // Update the buton log and notification
        updateLogState();
        updateTrackState();
        updateTrackLinkState();
    }

    @Override
    public void onResume(){
        super.onResume();
        updateLogState();
        updateTrackState();
        updateTrackLinkState();
    }

    public void updateLogState() {
        if (isLogAlarmUp()) {
            Log.d(TAG, "Alarm Log is already active");
            toggle_log.setChecked(true);
            showNotif();
        } else {
            Log.d(TAG, "Alarm Log is NOT active");
            toggle_log.setChecked(false);
            cancelNotif();
            if (sharedPref.getString("sessionID", null) != null) {
                // Remove Idsession
                editor.putString("sessionID", null).apply();
            }
        }
    }

    public void updateTrackState() {
        if (isTrackAlarmUp()) {
            Log.d(TAG, "Alarm  Track is already active");
            toggle_track.setChecked(true);

        } else {
            Log.d(TAG, "Alarm track is NOT active");
            toggle_track.setChecked(false);
        }
    }

    public void updateTrackLinkState() {
        if (isTrackAlarmUp()) {
            if (sharedPref.getString("trackingID", null) != null) {
                editText_track_link.setText( sharedPref.getString("trackingID", null)); // to add sharedPref.getString("serverURl_viewtrack", null)
            } else {
                editText_track_link.setText("Waiting for tracking ID ...");
            }
        } else {
            editText_track_link.setText("Tracking not active");
        }
    }


    public Boolean isLogAlarmUp() {
        Intent alertIntent = new Intent(this, GpsTrackerAlarmReceiver.class);
        return (PendingIntent.getBroadcast(this, 100, alertIntent, PendingIntent.FLAG_NO_CREATE)!=null);
    }

    public Boolean isTrackAlarmUp() {
        Intent alertIntentTrack = new Intent(this, TrackAlarmReceiver.class);
        return (PendingIntent.getBroadcast(this, 101, alertIntentTrack, PendingIntent.FLAG_NO_CREATE)!=null);
    }

    public Boolean checkLocationPermission(){
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    public void requestLocationPermission(){
        ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION }, LOCATION_PERMISSION_REQUEST_CODE);
    }



    public void toggleLogclick(View v){

        if(toggle_log.isChecked()) {

            if (checkLocationPermission()) {

                startAlarmManager_LOG();
                Toast.makeText(MainActivity.this, "Start Logging", Toast.LENGTH_SHORT).show();
            } else {
                requestLocationPermission();
            }

        } else {

            cancelAlarmManager_LOG();
            Toast.makeText(MainActivity.this, "End Logging", Toast.LENGTH_SHORT).show();
        }
    }

    public void toggleTrackclick(View v){

        if(toggle_track.isChecked()) {
            startAlarmManager_TRACK();
        } else {
            cancelAlarmManager_TRACK();
        }
    }

    public void mark_Point() {
        Log.d(TAG, "markPoint");

        if (!isLogAlarmUp()) {
            Log.d(TAG, "!isalarmUp()");
            editor.putString("sessionID", generatedSessionId()).apply();
        }

        if (sharedPref.getString("sessionID",null) == null) {
            Log.d(TAG, "SessionId == null");
            editor.putString("sessionID", generatedSessionId()).apply();
        }

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

    public void send_Point(){
        // Created the intent
        Intent intentsend=new Intent(this, TrackService.class);
        this.startService(intentsend);
    }


    private void startAlarmManager_LOG () {

        Log.d(TAG, "startAlarmManager_LOG interval :" + sharedPref.getInt("log_interval", 3));

        // Store Idsession
        editor.putString("sessionID", generatedSessionId()).apply();

        StartCancelRepeatingAlarm_LOG(this, true, sharedPref.getInt("log_interval", 3));

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


    private void cancelAlarmManager_LOG() {
        Log.d(TAG, "cancelAlarmManager_log");

        // Remove Idsession
        editor.putString("sessionID", null).apply();

        StartCancelRepeatingAlarm_LOG(this, false, sharedPref.getInt("log_interval", 3));
        //Clear Alarm
//        Context context = getBaseContext();
//        Intent gpsTrackerIntent = new Intent(context, GpsTrackerAlarmReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//        alarmManager.cancel(pendingIntent);

        cancelNotif();
    }




    private void startAlarmManager_TRACK() {
        if (isLogAlarmUp()) { //Check is the looging is on
            Log.d(TAG, "startAlarmManager_track interval: "+sharedPref.getInt("Tracking_interval",10));
            Toast.makeText(MainActivity.this, "Start Tracking", Toast.LENGTH_SHORT).show();
            StartCancelRepeatingAlarm_TRACK(this,true,sharedPref.getInt("Tracking_interval",10));
        } else {
            Log.d(TAG, "startAlarmManager_track Logging is not active , ABORT ");
            Toast.makeText(MainActivity.this, " Logging is not active, tracking impossible !", Toast.LENGTH_LONG).show();
        }
        updateTrackState();
    }

    private void cancelAlarmManager_TRACK() {
        Log.d(TAG, "cancelAlarmManager_track");
        editor.putString("trackingID", null).apply();
        Toast.makeText(MainActivity.this, "End Tracking", Toast.LENGTH_SHORT).show();
        StartCancelRepeatingAlarm_TRACK(this,false,sharedPref.getInt("Tracking_interval",10));
    }

    static void StartCancelRepeatingAlarm_LOG(Context context, boolean creating, int Interval) {
        //if it already exists, then replace it with this one
        Intent alertIntent = new Intent(context, GpsTrackerAlarmReceiver.class);
        PendingIntent timerAlarmIntent = PendingIntent.getBroadcast(context, 100, alertIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Log.d(TAG, "Log interval: "+Interval);

        if (creating) {
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), Interval * 60000, timerAlarmIntent);
        } else {
            timerAlarmIntent.cancel();
            alarmManager.cancel(timerAlarmIntent);
        }
    }

    static void StartCancelRepeatingAlarm_TRACK(Context context, boolean creating, int Interval) {
        //if it already exists, then replace it with this one
        Intent alertIntentTrack = new Intent(context, TrackAlarmReceiver.class);
        PendingIntent timerAlarmIntentTrack = PendingIntent.getBroadcast(context, 101, alertIntentTrack, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManagerTrack = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Log.d(TAG, "track interval: "+Interval);

        if (creating) {
            // Store Idsession

            alarmManagerTrack.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), Interval * 60000, timerAlarmIntentTrack);
        } else {
            timerAlarmIntentTrack.cancel();
            alarmManagerTrack.cancel(timerAlarmIntentTrack);
        }
    }


    //// Notifcation
    int mNotifID=1;    // Notification ID
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

    public void sharetrackID(){
        Intent intentShareText = new Intent(); intentShareText.setAction(Intent.ACTION_SEND);
        intentShareText.setType("text/plain");
        intentShareText.putExtra(Intent.EXTRA_TEXT, sharedPref.getString("msg_followme","")+": "+sharedPref.getString("serverURl_viewtrack","")+sharedPref.getString("sessionID","Not avalaible")); // TODO: 1/22/17  change session id to tracking ID 
        startActivity(Intent.createChooser(intentShareText, "Share via"));
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
        toggle_track=(ToggleButton) findViewById(R.id.toggleBtn_track);
        //Create button
        bttn_mark_pt = (Button) findViewById(R.id.bttn_mark_pt);
        bttn_send_pt=(Button) findViewById(R.id.button_send_track);  //use for debuging
        //Create Edit text
        editText_track_link= (EditText) findViewById(R.id.editText_track_link);
        //Created image button
        imageBttn_share=(ImageButton) findViewById(R.id.imageBttn_share);

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
