package fr.kriket.oso.model;

import java.util.Date;

/**
 * Created by fred on 12/4/16.
 * fr.kriket.oso
 */

public class SensorPosition {

    private int RowId;
    private Date DatePrise;
    private long TimeStamp;
    private float AccX;
    private float AccY;
    private float AccZ;
    private String Comment;


// CONSTRUCTOR

    public SensorPosition(long timeStamp, float accX, float accY, float accZ) {
        TimeStamp = timeStamp;
        AccX = accX;
        AccY = accY;
        AccZ = accZ;

        DatePrise=new Date();
        Comment=null;

    }


    // GETTERS and SETTERS

    public int getRowId() {
        return RowId;
    }

    public void setRowId(int rowId) {
        RowId = rowId;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public float getAccZ() {
        return AccZ;
    }

    public void setAccZ(float accZ) {
        AccZ = accZ;
    }

    public float getAccY() {
        return AccY;
    }

    public void setAccY(float accY) {
        AccY = accY;
    }

    public float getAccX() {
        return AccX;
    }

    public void setAccX(float accX) {
        AccX = accX;
    }

    public float getTimeStamp() {
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
}
