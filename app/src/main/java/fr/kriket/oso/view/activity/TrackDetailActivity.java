package fr.kriket.oso.view.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import javax.xml.datatype.Duration;

import fr.kriket.oso.R;
import fr.kriket.oso.model.Track;
import fr.kriket.oso.tools.GpsTools;

import static fr.kriket.oso.tools.Timetools.formatSeconds;


public class TrackDetailActivity extends AppCompatActivity {

    private static final String TAG = "TrackDetailActi";


    TextView txtview_nbPoints;
    TextView txtview_startDate;
    TextView txtview_endDate;
    TextView txtview_duration;
    TextView txtview_sessionID;
    TextView textView_TrackingId;

    TextView txtview_distCurvi;
    TextView txtview_distLinear;
    TextView textView_LogInterval;

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

        NumberFormat formatter = new DecimalFormat("0.000");

        txtview_nbPoints.setText(track.getNbSent()+"/"+track.getLength());
        txtview_startDate.setText(formatDate.format(track.getFirstDate()));
        txtview_endDate.setText(formatDate.format(track.getLastDate()));
        txtview_duration.setText(formatSeconds(track.getDuration()));
        textView_LogInterval.setText(String.valueOf(track.getLogInterval()/1000)+ " min");
        txtview_sessionID.setText(track.getsessionID());
        textView_TrackingId.setText(track.getTrackingID());
        txtview_distLinear.setText(String.valueOf(formatter.format(GpsTools.distLineTrack(track)/1000)));
        txtview_distCurvi.setText(String.valueOf(formatter.format(GpsTools.distCurviTrack(track)/1000)));
    }



    /// Initialisation
    private void findViewsById() {

        //Create Textview

        txtview_nbPoints= (TextView) findViewById(R.id.textView_nbPoints);
        txtview_startDate= (TextView) findViewById(R.id.textView_StartDate);
        txtview_endDate= (TextView) findViewById(R.id.textView_endDate);
        txtview_duration = (TextView) findViewById(R.id.textView_duration);
        txtview_sessionID= (TextView) findViewById(R.id.textView_sessionId);
        textView_TrackingId= (TextView) findViewById(R.id.textView_TrackingId);
        txtview_distCurvi= (TextView) findViewById(R.id.textView_dist);
        txtview_distLinear= (TextView) findViewById(R.id.textView_dist_lin);
        textView_LogInterval=(TextView) findViewById(R.id.textView_LogInterval);

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
