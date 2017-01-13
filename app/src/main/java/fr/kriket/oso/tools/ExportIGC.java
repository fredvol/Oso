package fr.kriket.oso.tools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;

import fr.kriket.oso.model.SensorPosition;
import fr.kriket.oso.model.Track;
import fr.kriket.oso.model.TrackPoint;

/**
 * Created by fred on 1/13/17.
 */

public class ExportIGC {

    // TODO: 1/13/17 add G line , signature

    public static String generateIGCFile(Context context, Track track, String filename) {
        File path = context.getExternalFilesDir(null);
        File file = new File(path, filename);
        try {
            file.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        SimpleDateFormat formatDate = new SimpleDateFormat("HHmmss");
        String output = initiateIGCfile(track);

        Log.i("GeneratedIGC > path", path.toString());
        Log.i("GeneratedIGC > file", file.toString());


        for (TrackPoint trackPoint : track.getTrackPoints()) {

            output += "B"
                    + formatDate.format(trackPoint.getDate())
                    + formatIGCLat(trackPoint.getLati())
                    +formatIGCLong(trackPoint.getLong())
                    +"A"
                    +"00000"
                    + formatIGCAltitude(trackPoint.getAlt())+ "\n";
        }


        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {

            stream.write(output.getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return file.toString();
    }

    public static String initiateIGCfile(Track track){


        SimpleDateFormat formatDate = new SimpleDateFormat("ddMMyy");

// TODO: 1/13/17  add real information
        String Headerstr = "AXXX00000"+ "\n"
                +"HFFXA035"+ "\n"
                +"HFDTE"+formatDate.format(track.getFirstDate())+ "\n"
                +"HFDTM100GPSDATUM:WGS-1984"+ "\n"
                +"HFRFWFIRMWAREVERSION:1"+ "\n"
                +"HFGPS:internal"+ "\n"
                +"HFPRSPRESSALTSENSOR:internal"+ "\n"
                +"";
        return Headerstr;
    }

    public static String formatIGCAltitude(double alt){
        int altint= (int) alt;
        String altstr=String.valueOf(altint);

        while (altstr.length()<5){
            altstr="0"+altstr;
        }
        return altstr;
    }

    // TODO: 1/13/17 to improve not clean
    public static String formatIGCLat(double latitude){
        String latstr=null;

        long iPart = (long) latitude;
        double fPart = (latitude - iPart)*60;

        String iPartstr= String.valueOf(iPart);

        String fPartstr=String.valueOf((long) (fPart*1000));

        while (iPartstr.length()<2){
            iPartstr="0"+iPartstr;
        }

        while (fPartstr.length()<5){
            fPartstr="0"+fPartstr;
        }

        latstr=iPartstr+fPartstr;

            if(latitude>0){
                latstr=latstr+"N";
            }else {
                latstr=latstr+"S";
            }

        return latstr;
    }

    public static String formatIGCLong(double longitude){
        String longstr=null;



        // TODO: 1/13/17 to improve not clean

        long iPart = (long) longitude;
        double fPart = (longitude - iPart)*60;

        String iPartstr= String.valueOf(iPart);

        String fPartstr=String.valueOf((long) (fPart*1000));

        while (iPartstr.length()<3){
            iPartstr="0"+iPartstr;
        }

        while (fPartstr.length()<5){
            fPartstr="0"+fPartstr;
        }

        longstr=iPartstr+fPartstr;


            if(longitude>0){
                longstr=longstr+"E";
            }else {
                longstr=longstr+"W";
            }


        return longstr;
    }



}
