package fr.kriket.oso.view.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import fr.kriket.oso.R;
import fr.kriket.oso.loader.internal.GetTrackBookLoader;
import fr.kriket.oso.loader.internal.TracksLoader;
import fr.kriket.oso.model.Track;
import fr.kriket.oso.model.TrackPoint;

public class TrackBookActivity extends AppCompatActivity implements GetTrackBookLoader.GetTrackBookLoaderListener{


    private static final String TAG = "TrackBookActivity";

    ListView mListView;
private List<Track> alltracks= new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_book);

        findViewsById();
        //Format title bar
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Track Book");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        refreshData();
    }




    private void refreshData() {
        GetTrackBookLoader loader = new GetTrackBookLoader(this, this);
        loader.execute();

    }

    /// Initialisation
    private void findViewsById() {
        //Create Listview
        mListView = (ListView) findViewById(R.id.listview_trackbook);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
                return true;

        }
        return false;
    }


    @Override
    public void onStartGetTrackBook() {

    }

    @Override
    public void onGetTrackBookSucess(List list) {
        Log.d(TAG,"onGetTrackBookSucess: "+ list);

        Log.d(TAG,"onGetTrackBookSucessUnikID: "+ ExtractUnikSessionID(list));
        alltracks=Trackpoint2Track(list,ExtractUnikSessionID(list));
        Log.d(TAG,"onGetTrackBookSucessalltrack: "+ alltracks);

        // add number of track
        getSupportActionBar().setSubtitle("Number of tracks: "+alltracks.size());

        ArrayAdapter<Track> adapter;
        adapter = new TracksLoader(this, alltracks);
        mListView.setAdapter(adapter); // remove comment above

    }

    @Override
    public void onGetTrackBookFailed(String s) {
        Log.d(TAG,"onGetTrackBookFailed: "+ s);

    }

    private List<String> ExtractUnikSessionID(List<TrackPoint> trackPoints){
        String mTAG = "TrckBkAct/Trckpt2Track";
        Log.i(mTAG, " start: "+ trackPoints);

        List<String> mUniqueSessionID = new ArrayList<>();
      // create list with duplicates...
        for (TrackPoint trackPoint: trackPoints) {
            if (!mUniqueSessionID.contains(trackPoint.getSessionId())) {
                mUniqueSessionID.add(trackPoint.getSessionId());
            }
        }
        return mUniqueSessionID;
    }

    private List<Track> Trackpoint2Track(List<TrackPoint> trackPoints ,List<String> uniqueSessionID){
        List<Track> tracks = new ArrayList<>();
        for (String mID : uniqueSessionID) {
            List<TrackPoint> mtrackPoints=new ArrayList<>();
            for (TrackPoint trackPoint: trackPoints) {
                if (mID.equals(trackPoint.getSessionId())) {
                    mtrackPoints.add(trackPoint);
                }
            }
            tracks.add(new Track(mtrackPoints));

        }
        return tracks;
    }
}
