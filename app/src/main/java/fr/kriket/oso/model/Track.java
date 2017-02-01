package fr.kriket.oso.model;

import android.content.Context;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import android.preference.PreferenceManager;
import android.util.Log;


/**
 * Created by fred on 1/7/17.
 */


public class Track implements Serializable {



    private  List<TrackPoint> trackPoints;

    // CONSTRUCTOR


    public Track(List<TrackPoint> trackPoints) {
        this.trackPoints = trackPoints;
    }


    // Getter and setter

    public List<TrackPoint> getTrackPoints() {
        return trackPoints;
    }

    public void setTrackPoints(List<TrackPoint> trackPoints) {
        this.trackPoints = trackPoints;
    }

    public long getLength() {
        return  this.trackPoints.size();
    }

    public String getsessionID() {
        return  this.trackPoints.get(0).getSessionId();
    }

    public Date getFirstDate() {
        return  this.trackPoints.get(0).getDate();
    }

    public Date getLastDate() {
        return  this.trackPoints.get(this.trackPoints.size()-1).getDate();
    }

    public long getNbSent(){
        long count = 0;
        for (TrackPoint Tpt : this.trackPoints  ){
            if(Tpt.isSent()){
                count++;
            }
        }
        return count;
    }

    public long getLogInterval(){
        return  Math.round((this.getDuration()/this.getLength())/60);
    }

    public long getDuration() {
        return  this.trackPoints.get(this.trackPoints.size()-1).getTimeStamp()-this.trackPoints.get(0).getTimeStamp();
    }

    public boolean isValid() {
        if(this.trackPoints.size()==0) return false;
        if (this.getsessionID()== null) return false;

        return true;
    }

    public boolean isActive(Context mContex) {

       return  (PreferenceManager.getDefaultSharedPreferences(mContex).getString("sessionID","").equals(this.getsessionID()));
    }
}
