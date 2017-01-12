package fr.kriket.oso.view.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import fr.kriket.oso.R;

public class TrackDetailActivity extends AppCompatActivity {

    private static final String TAG = "TrackDetailActi";

    TextView txtview_nbPoints;
    TextView txtview_startDate;
    TextView txtview_endDate;
    TextView txtview_duration;
    TextView txtview_sessionID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_detail);

        findViewsById();

        //Format title bar
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Track Detail:");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
