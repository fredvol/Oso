package fr.kriket.oso.model;

import java.util.Date;

/**
 * Created by fred on 1/1/17.
 */

public class TrackPoint {

    private long RowId;
    private String UserId;
    private Date DatePrise;
    private long TimeStamp;
    private float Lat;
    private float Long;
    private float Alt;
    private int Bat;
    private int NetworkStrength;
    private String Comment;
    private boolean isSent;


    // CONSTRUCTOR


    public TrackPoint(long rowId, String userId, Date datePrise, long timeStamp, float lat, float aLong, float alt, int bat, int networkStrength, String comment, boolean isSent) {
        RowId = rowId;
        UserId = userId;
        DatePrise = datePrise;
        TimeStamp = timeStamp;
        Lat = lat;
        Long = aLong;
        Alt = alt;
        Bat = bat;
        NetworkStrength = networkStrength;
        Comment = comment;
        this.isSent = isSent;
    }

    public TrackPoint(long timeStamp, float lat, float aLong, float alt, int bat, int networkStrength) {
        TimeStamp = timeStamp;
        Lat = lat;
        Long = aLong;
        Alt = alt;
        Bat = bat;
        NetworkStrength = networkStrength;
    }

    // GETTERS and SETTERS

    public long getRowId() {
        return RowId;
    }

    public void setRowId(long rowId) {
        RowId = rowId;
    }

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

    public float getAlt() {
        return Alt;
    }

    public void setAlt(float alt) {
        Alt = alt;
    }

    public int getBat() {
        return Bat;
    }

    public void setBat(int bat) {
        Bat = bat;
    }

    public float getLong() {
        return Long;
    }

    public void setLong(float aLong) {
        Long = aLong;
    }

    public float getLat() {
        return Lat;
    }

    public void setLat(float lat) {
        Lat = lat;
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

    @Override
    public String toString() {
        return "TrackPoint{" +
                "RowId=" + RowId +
                ", DatePrise=" + DatePrise +
                ", TimeStamp=" + TimeStamp +
                ", Lat=" + Lat +
                ", Long=" + Long +
                ", Alt=" + Alt +
                ", Bat=" + Bat +
                ", NetworkStrength=" + NetworkStrength +
                ", Comment='" + Comment + '\'' +
                ", isSent=" + isSent +
                '}';
    }
}
