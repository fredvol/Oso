package fr.kriket.oso.view.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import fr.kriket.oso.R;
import fr.kriket.oso.controler.sqlite.DatabaseHandler;
import fr.kriket.oso.loader.internal.GetTrackBookLoader;
import fr.kriket.oso.loader.internal.TracksLoader;
import fr.kriket.oso.model.Track;
import fr.kriket.oso.model.TrackPoint;

import static fr.kriket.oso.controler.sqlite.DatabaseHandler.TRACKPT_TABLE_NAME;
import static fr.kriket.oso.tools.ExportIGC.generateIGCFile;
import static fr.kriket.oso.tools.GenerateCsv.generateCsvFile;

public class TrackBookActivity extends AppCompatActivity implements GetTrackBookLoader.GetTrackBookLoaderListener{


    private static final String TAG = "TrackBookActivity";
    public static final String TRACKPT_SESSIONID = "SessionId";

    ListView mListView;
    Context mcontext =this;

private List<Track> alltracks= new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_book);


        findViewsById();

        // Register to display the long click context menu
        registerForContextMenu(mListView);

        //Format title bar
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Track Book");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        refreshData();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.i(TAG, "Click");
                String stringTrack;

                stringTrack= alltracks.get(position).getsessionID();

                Track track=alltracks.get(position);
                // Start activity modif


                Log.i(TAG, "Track Selected: "+stringTrack);



                Intent StartTrackDetailActivite = new Intent(mcontext, TrackDetailActivity.class);
                StartTrackDetailActivite.putExtra("Track", (Serializable) track);
                startActivity(StartTrackDetailActivite);


            }

        });

    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.listview_trackbook) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.long_click_track_list_menu, menu);
        }
        menu.setHeaderTitle("Track Option:");
    }

    // TODO: 1/15/17 add submenu for export 
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.lg_clik_track_view:
                Log.d(TAG,"after menu : view" );
//                // add stuff here
                return true;
            case R.id.lg_clik_track_delete:
                Log.d(TAG,"after menu : delete" );

                if (deleteTrackinDB(alltracks.get(info.position).getsessionID())){

                    Toast toast = Toast.makeText(mcontext,"Session deleted !",Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(mcontext,"Ooops , An error occur!",Toast.LENGTH_SHORT);
                    toast.show();
                }
                return true;

            case R.id.lg_clik_export_igc:
                Log.d(TAG,"after menu : export Igc" );
                DateFormat dateFormatIGC = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");

                String fileName= dateFormatIGC.format(alltracks.get(info.position).getFirstDate())+".igc";
                String igcPath= generateIGCFile(this,alltracks.get(info.position),fileName);

                Toast toast = Toast.makeText(mcontext,"IGC saved !  : " +igcPath,Toast.LENGTH_SHORT);
                toast.show();
               return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private boolean deleteTrackinDB(String sessionID){
        Log.d(TAG,"Delete session IS : "+sessionID );

        DatabaseHandler mDbHelper = new DatabaseHandler(mcontext,TRACKPT_TABLE_NAME,null,1);


        final String TRACKPT_TABLE_NAME = "TrackPointTable";

        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        boolean isdelete = db.delete(TRACKPT_TABLE_NAME, TRACKPT_SESSIONID + "='" + sessionID+"'", null) > 0;
        db.close();

        return isdelete;

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

        //Sort  Track from Newest to older


        Collections.sort(alltracks, new Comparator<Track>() {

            public int compare(Track o1, Track o2) {
                return o2.getFirstDate().compareTo(o1.getFirstDate());
            }
        });


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
                //Log.d(TAG,"trck2point mId="+mID +" tracpt.sessID="+trackPoint.getSessionId());

                if (trackPoint.isValid() && mID != null) {
                    try {
                        if (mID.equals(trackPoint.getSessionId())) {
                            mtrackPoints.add(trackPoint);
                        }
                    } catch (Exception e) {

                        e.printStackTrace();       // FIXME: 1/9/17  if element is null mID
                    }
                }

            }
            if (mtrackPoints.size()>0) {
                tracks.add(new Track(mtrackPoints));
            }

        }
        return tracks;
    }
}
