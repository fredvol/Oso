package fr.kriket.oso.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by fred on 1/1/17.
 */


public class TrackPoint implements Serializable {

    private long RowId;
    private String UserId;
    private String TrackingId;
    private String SessionId;
    private Date DatePrise;
    private long TimeStamp;
    private double Lati;
    private double Long;
    private double Alt;
    private float Acc;
    private int Bat;
    private int NetworkStrength;
    private String Comment;
    private boolean isSent;


    // CONSTRUCTOR


    public TrackPoint(long rowId, String userId, String sessionId, Date datePrise, long timeStamp, double lati, double aLong, double alt, int bat,float acc, int networkStrength, String comment, boolean isSent) {
        RowId = rowId;
        UserId = userId;
        SessionId = sessionId;
        DatePrise = datePrise;
        TimeStamp = timeStamp;
        Lati = lati;
        Long = aLong;
        Alt = alt;
        Bat = bat;
        Acc=acc;
        NetworkStrength = networkStrength;
        Comment = comment;
        this.isSent = isSent;
    }

    public TrackPoint(String sessionId, long timeStamp, Date datePrise, double lat, double aLong, double alt, float acc) {
        SessionId = sessionId;
        TimeStamp = timeStamp;
        DatePrise = datePrise;
        Lati = lat;
        Long = aLong;
        Acc=acc;
        Alt = alt;
    }



// GETTERS and SETTERS


    public Date getDate(){return new Date(TimeStamp); }

    public long getRowId() {
        return RowId;
    }

    public void setRowId(long rowId) {RowId = rowId; }

    public String getSessionId() {return SessionId; }

    public void setSessionId(String sessionId) {SessionId = sessionId;}

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public int getNetworkStrength() {
        return NetworkStrength;
    }

    public void setNetworkStrength(int networkStrength) {
        NetworkStrength = networkStrength;
    }

    public double getAlt() {
        return Alt;
    }

    public void setAlt(float alt) {
        Alt = alt;
    }

    public float getAcc() { return Acc; }

    public void setAcc(float acc) { Acc = acc; }

    public int getBat() {
        return Bat;
    }

    public void setBat(int bat) {
        Bat = bat;
    }

    public double getLong() {
        return Long;
    }

    public void setLong(float aLong) {
        Long = aLong;
    }

    public double getLati() {
        return Lati;
    }

    public void setLati(float lati) {
        Lati = lati;
    }

    public long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        TimeStamp = timeStamp;
    }

    public Date getDatePrise() {
        return DatePrise;
    }

    public void setDatePrise(Date datePrise) {
        DatePrise = datePrise;
    }

    public String getTrackingId() { return TrackingId; }

    public void setTrackingId(String trackingId) { TrackingId = trackingId;    }

    // Method
    public boolean isValid() {
        if(this.getSessionId()==null) return false;
        if ( this.getTimeStamp()==0) return false;

        return true;
    }

    @Override
    public String toString() {
        return "TrackPoint{" +
                "RowId=" + RowId +
                ", UserId='" + UserId + '\'' +
                ", SessionId='" + SessionId + '\'' +
                ", DatePrise=" + DatePrise +
                ", TimeStamp=" + TimeStamp +
                ", Lati=" + Lati +
                ", Long=" + Long +
                ", Alt=" + Alt +
                ", Bat=" + Bat +
                ", NetworkStrength=" + NetworkStrength +
                ", Comment='" + Comment + '\'' +
                ", isSent=" + isSent +
                '}';
    }
}
