package fr.kriket.oso.view.activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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

import fr.kriket.oso.R;
import fr.kriket.oso.controler.internal.GpsTrackerAlarmReceiver;
import fr.kriket.oso.controler.internal.TrackAlarmReceiver;
import fr.kriket.oso.service.LocationService;
import fr.kriket.oso.service.TrackService;

import static fr.kriket.oso.tools.random.randInt;
import static fr.kriket.oso.tools.random.rndChar;


// TODO: 2/1/17 find a way to change the intervals ( log & send) during the tracking

// TODO: 1/12/17 pause tracking

/**
 * Note : for the moment the app is using the SessionID as Tracking ID ( it request a tracking ID but it useless)
 */


public class MainActivity extends AppCompatActivity  {

    private static final String TAG = "MainActivity";



    ToggleButton toggle_log;

    ToggleButton toggle_track;

    Button bttn_mark_pt;

    Button bttn_send_pt;

    EditText editText_track_link;

    ImageButton imageBttn_share;



    final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    // Reveiced broad cast
    private final String ACTION_RECEIVE_TRACKINGID = "fr.kriket.oso.view.activity.MainActivity.TRACKID_RECEIVED";

    // SHared preference management
    private SharedPreferences sharedPref ;
    SharedPreferences.Editor editor;

    private BroadcastReceiver updateUIReceiver;


    protected LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Share pref
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //editor
        editor = sharedPref.edit();

        //Initialise all variable if first start
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        findViewsById();


        // Broadcast receiver from  track service.GetTrakingID  ( to update UI)
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(ACTION_RECEIVE_TRACKINGID);
//
        setUpdateUIReceiver();
//        updateUIReceiver = new BroadcastReceiver() {
//
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                Log.d(TAG, "onReceive Broadcast  get Track ID  so update UI");
//                updateUI();
//
//            }
//        };
//        registerReceiver(updateUIReceiver,filter);


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
                mark_Point(true);

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


        editText_track_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "click on adresse");
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(editText_track_link.getText().toString())));
            }
        });


        // Update the User interface
        updateUI();
    }



    @Override
    public void onResume(){
        super.onResume();
        setUpdateUIReceiver();

        updateUI();
    }

    public void setUpdateUIReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_RECEIVE_TRACKINGID);
        updateUIReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive Broadcast  get Track ID  so update UI");
                updateUI();

            }
        };
        registerReceiver(updateUIReceiver,filter);
    }

    @Override
    public void onPause(){
        super.onPause();
        if (updateUIReceiver!=null) {     // TODO: 2/7/17  need to be check more , nut sure is working
            Log.d(TAG, "unregisterReceiver(updateUIReceiver)");

            try {
                unregisterReceiver(updateUIReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
            updateUIReceiver=null;
        }
    }

    // TODO: 2/7/17


    public void updateUI() {
        updateLogState();
        updateTrackState();
        updateTrackLinkState();
    }


    /**
     * Update log state.
     * Update the toggle button state and the notification in task bar, if the log alarm is running
     * if th alarm is not running it also remove the sessionID just in case.
     */
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

    /**
     * Update track state.
     * Like Update log state with Track alarm and track button
     */
    // TODO: 2/1/17 add the tracking state in the notification
    public void updateTrackState() {
        if (isTrackAlarmUp()) {
            Log.d(TAG, "Alarm  Track is already active");
            toggle_track.setChecked(true);

        } else {
            Log.d(TAG, "Alarm track is NOT active");
            toggle_track.setChecked(false);
        }
    }

    /**
     * Update track link state.
     * update the edit text with the Url  to see the tracking
     */


    public void updateTrackLinkState() {
        if (isTrackAlarmUp()) {
            if (sharedPref.getString("trackingID", null) != null) {
                editText_track_link.setText(sharedPref.getString("serverURl_viewtrack", null)+sharedPref.getString("trackingID", null)); // to add sharedPref.getString("serverURl_viewtrack", null);    sharedPref.getString("trackingID",null)
                //editText_track_link.setClickable(true);
                editText_track_link.setEnabled(true);
            } else {
                editText_track_link.setText("Waiting for tracking ID ...");
                //editText_track_link.setClickable(false);
                editText_track_link.setEnabled(false);
            }
        } else {
            editText_track_link.setText("Tracking not active");
            //editText_track_link.setClickable(false);
            editText_track_link.setEnabled(false);
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


    /**
     * Test if the net work is available ( wifi or Data mobile)
     * @return bollean
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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

    /**
     * Mark point.
     * Call the Location Service to add a point.
     * Ask if the user want to add a optional comment
     */
    public void mark_Point(Boolean withcomment) {
        Log.d(TAG, "markPoint");

        if (!isLogAlarmUp()) {                // if the user was not in tracking but want to add a single point
            Log.d(TAG, "!isalarmUp()");
            editor.putString("sessionID", generatedSessionId()).apply();
        }

        if (sharedPref.getString("sessionID",null) == null) {
            Log.d(TAG, "SessionId == null");
            editor.putString("sessionID", generatedSessionId()).apply();
        }

        if (withcomment) {
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
                    .setPositiveButton("Save",
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
        } else {
            lauchMarkPointIntent(null);
        }

    }


    public void lauchMarkPointIntent(String mextracomment){
        // Created the intent
        Intent intentlocat=new Intent(this, LocationService.class);

        intentlocat.putExtra("isMarkPoint",true);    // Boolen to display a Toast when the mark point is added
        intentlocat.putExtra("extraComment",mextracomment);
        this.startService(intentlocat);
    }

    /**
     * Send point.
     * Call the Track Service to force to send points.
     */
    public void send_Point(){
        // Created the intent
        Intent intentsend=new Intent(this, TrackService.class);

        intentsend.putExtra("imposeTrackingID", sharedPref.getString("sessionID", null));
        this.startService(intentsend);
    }


    /** Start of the LOGGING
     */
    private void startAlarmManager_LOG() {
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d(TAG, "startAlarmManager_LOG interval :" + sharedPref.getInt("log_interval", 3));

            // Store Idsession
            editor.putString("sessionID", generatedSessionId()).apply();

            StartCancelRepeatingAlarm_LOG(this, true, sharedPref.getInt("log_interval", 3));
            mark_Point(false);
        } else {
            // display asking to enable GPS
            android.support.v7.app.AlertDialog.Builder builder;
            builder = new android.support.v7.app.AlertDialog.Builder(this);
            builder.setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("GPS ?")
                    .setMessage(" GPS Not enable!  \n \n  Please active  it ...")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }
        updateUI();
        showNotif();
    }


    /*** Stop the LOGGING
     */
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
        updateUI();
        cancelNotif();
    }

    /** Start of the TRACKING
     */
    private void startAlarmManager_TRACK() {
        if (isNetworkAvailable()) {
            startAlarmManager_LOG();
            if (isLogAlarmUp()) { //Check is the looging is on
                Log.d(TAG, "startAlarmManager_track interval: " + sharedPref.getInt("Tracking_interval", 10));
                Toast.makeText(MainActivity.this, "Start Tracking", Toast.LENGTH_SHORT).show();
                StartCancelRepeatingAlarm_TRACK(this, true, sharedPref.getInt("Tracking_interval", 10));
            } else {
                Log.d(TAG, "startAlarmManager_track Logging is not active , ABORT ");
                Toast.makeText(MainActivity.this, " Logging is not active, tracking impossible !", Toast.LENGTH_LONG).show();
            }

        } else {
            // display asking to enable data connexion
            android.support.v7.app.AlertDialog.Builder builder;
            builder = new android.support.v7.app.AlertDialog.Builder(this);
            builder.setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Data Connexion ?")
                    .setMessage(" No data connexion!  \n \n  Please enable them ...")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }
        updateUI();
    }

    /*** Stop the TRACKING
     */
    private void cancelAlarmManager_TRACK() {
        Log.d(TAG, "cancelAlarmManager_track");
        send_Point(); //send the last points   sharedPref.getString("sessionID", null)
        cancelAlarmManager_LOG();
        editor.putString("trackingID", null).apply();
        Toast.makeText(MainActivity.this, "End Tracking", Toast.LENGTH_SHORT).show();
        StartCancelRepeatingAlarm_TRACK(this,false,sharedPref.getInt("Tracking_interval",10));

        updateUI();
    }

    /**
     * Start cancel repeating alarm log.
     *
     * @param context  the context
     * @param creating Bool creating ( or cancel)
     * @param Interval the interval in minute
     */
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

    /**
     * Start cancel repeating alarm track.
     *
     * @param context  the context
     * @param creating Bool creating ( or cancel)
     * @param Interval the interval in minute
     */
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


    /**
     * The notif management.
     */
//// Notifcation
    int mNotifID=1;    // Notification ID

    private void showNotif() {
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

    private void cancelNotif() {
        // Remove Notification
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(mNotifID);
    }

    /**
     * Sharetrack id.
     */
    public void sharetrackID(){
        Intent intentShareText = new Intent(); intentShareText.setAction(Intent.ACTION_SEND);
        intentShareText.setType("text/plain");
        intentShareText.putExtra(Intent.EXTRA_TEXT, sharedPref.getString("msg_followme","")+": "+sharedPref.getString("serverURl_viewtrack","")+sharedPref.getString("trackingID","Not avalaible"));
        startActivity(Intent.createChooser(intentShareText, "Share via"));
 }


    /**
     * Generated an ID for the session
     * @return a string  from aa000aa to ZZ999ZZ
     */
    private String generatedSessionId(){

     return  String.valueOf(rndChar())+
             String.valueOf(rndChar())+
             String.valueOf(randInt(0,9))+
             String.valueOf(randInt(0,9))+
             String.valueOf(randInt(0,9))+
             String.valueOf(rndChar())+
             String.valueOf(rndChar());
    }



    /**
     * Activity layout initialisation
     */
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
