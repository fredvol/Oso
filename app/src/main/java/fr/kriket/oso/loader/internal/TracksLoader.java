package fr.kriket.oso.loader.internal;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import fr.kriket.oso.R;
import fr.kriket.oso.model.Track;

import static fr.kriket.oso.tools.Timetools.formatSeconds;

/**
 * Created by fred on 1/7/17.
 */


public class TracksLoader extends ArrayAdapter<Track> {



    private Context mContext;
    SimpleDateFormat formatdNow = new SimpleDateFormat("dd/MM/yy' - 'HH:mm");


    public TracksLoader(Context context, List<Track> tracks) {
        super(context, 0, tracks);
        mContext = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_tracks, parent, false);
        }

        TracksLoaderHolder viewHolder = (TracksLoaderHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new TracksLoaderHolder();
            viewHolder.NB = (TextView) convertView.findViewById(R.id.txtView_NB);
            viewHolder.TrackingID = (TextView) convertView.findViewById(R.id.txtView_TrackingId);
            viewHolder.StartDate = (TextView) convertView.findViewById(R.id.txtView_StartDate);
            viewHolder.EndDate = (TextView) convertView.findViewById(R.id.txtView_EndDate);

            convertView.setTag(viewHolder);

        }

        //getItem(position) get the item[position] of the List<Track> track
        Track track = getItem(position);

        //populated field

        viewHolder.NB.setText(track.getNbSent()+" / "+track.getLength());
        viewHolder.TrackingID.setText(track.getTrackingID());
        viewHolder.StartDate.setText(formatdNow.format(track.getLastDate()));
        viewHolder.EndDate.setText(formatSeconds(track.getDuration()));
        //viewHolder.StartDate.setText(formatdNow.format(track.getFirstDate()));


        // color if today
        if ( track.isActive(mContext)){
            convertView.setBackgroundColor(Color.argb(120,11,227,51));
        } else {
            convertView.setBackgroundColor(Color.argb(25,230,230,230));
        }

        return convertView;
    }

    private class TracksLoaderHolder {
        public TextView NB;
        public TextView TrackingID;
        public TextView StartDate;
        public TextView EndDate;

    }
}
