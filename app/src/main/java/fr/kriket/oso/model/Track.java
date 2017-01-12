package fr.kriket.oso.model;

import android.content.Context;

import java.util.Date;
import java.util.List;

import fr.kriket.oso.tools.SharedPreference;

/**
 * Created by fred on 1/7/17.
 */



public class Track {



    private  List<TrackPoint> trackPoints;

    // CONSTRUCTOR


    public Track(List<TrackPoint> trackPoints) {
        this.trackPoints = trackPoints;
    }

    public List<TrackPoint> getTrackPoints() {
        return trackPoints;
    }

    public void setTrackPoints(List<TrackPoint> trackPoints) {
        this.trackPoints = trackPoints;
    }

    public int getLength() {
        return  this.trackPoints.size();
    }

    public String getsessionID() {
        return  this.trackPoints.get(0).getSessionId();
    }

    public Date getFirstDate() {
        return  this.trackPoints.get(0).getDatePrise();
    }

    public Date getLastDate() {
        return  this.trackPoints.get(this.trackPoints.size()-1).getDatePrise();
    }


    public boolean isValid() {
        if(this.trackPoints.size()==0) return false;
        if (this.getsessionID()== null) return false;

        return true;
    }

    public boolean isActive(Context mContex) {
       return  (mContex.getSharedPreferences("OSO_PREFS",Context.MODE_PRIVATE).getString("SessionId","").equals(this.getsessionID())); // FIXME: 1/12/17     // TODO: 1/12/17 HERE soleve acces shared pref
    }
}
