package fr.kriket.oso.view.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import javax.xml.datatype.Duration;

import fr.kriket.oso.R;
import fr.kriket.oso.model.Track;


public class TrackDetailActivity extends AppCompatActivity {

    private static final String TAG = "TrackDetailActi";


    TextView txtview_nbPoints;

    TextView txtview_startDate;

    TextView txtview_endDate;

    TextView txtview_duration;

    TextView txtview_sessionID;

    Track track;

    /**
     * The Format date.
     */
    SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yy' - 'HH:mm");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_detail);

        findViewsById();

        track= (Track) getIntent().getSerializableExtra("Track");

        //Format title bar
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Track Detail:");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        displayData();
    }

    /// Initialisation
    private void displayData() {


         txtview_nbPoints.setText(track.getNbSent()+"/"+track.getLength());
         txtview_startDate.setText(formatDate.format(track.getFirstDate()));
         txtview_endDate.setText(formatDate.format(track.getLastDate()));
         txtview_duration.setText(formatSeconds(track.getDuration()));
         txtview_sessionID.setText(track.getsessionID());

    }


    /**
     * Format seconds string.
     *
     * To be changed.
     *
     * @param timeInMiliSeconds the time in miliseconds
     * @return the string
     */
    public static String formatSeconds(long timeInMiliSeconds)
    {
        long timeInSeconds=timeInMiliSeconds/1000;

        long hours = timeInSeconds / 3600;
        long secondsLeft = timeInSeconds - hours * 3600;
        long minutes = secondsLeft / 60;
        long seconds = secondsLeft - minutes * 60;

        String formattedTime = "";
        if (hours < 10)
            formattedTime += "0";
        formattedTime += hours + ":";

        if (minutes < 10)
            formattedTime += "0";
        formattedTime += minutes + ":";

        if (seconds < 10)
            formattedTime += "0";
        formattedTime += seconds ;

        return formattedTime;
    }

    /// Initialisation
    private void findViewsById() {

        //Create Textview

        txtview_nbPoints= (TextView) findViewById(R.id.textView_nbPoints);
        txtview_startDate= (TextView) findViewById(R.id.textView_StartDate);
        txtview_endDate= (TextView) findViewById(R.id.textView_endDate);
        txtview_duration = (TextView) findViewById(R.id.textView_duration);
        txtview_sessionID= (TextView) findViewById(R.id.textView_sessionId);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
