package fr.kriket.oso.model;

import java.util.Date;
import java.util.List;

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


}
