package fr.kriket.oso.tools;

/**
 * Created by fred on 1/24/17.
 */


import fr.kriket.oso.model.Track;
import fr.kriket.oso.model.TrackPoint;

public class GpsTools {
    public static double gps2m(double lat_a, double lng_a, double lat_b, double lng_b) {
        double pk = (180/3.14169);

        double a1 = lat_a / pk;
        double a2 = lng_a / pk;
        double b1 = lat_b / pk;
        double b2 = lng_b / pk;

        double t1 = Math.cos(a1)*Math.cos(a2)*Math.cos(b1)*Math.cos(b2);
        double t2 = Math.cos(a1)*Math.sin(a2)*Math.cos(b1)*Math.sin(b2);
        double t3 = Math.sin(a1)*Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000*tt;
    }

    public static double dist2pt(TrackPoint Tpt1 ,TrackPoint Tpt2){
        return gps2m(Tpt1.getLati(),Tpt1.getLong(),Tpt2.getLati(),Tpt2.getLong());
    }

    public static double distLineTrack(Track track) {
        return dist2pt(track.getTrackPoints().get(0),track.getTrackPoints().get(track.getTrackPoints().size()-1));
    }

    public static double distCurviTrack(Track track) {
        double disttotal=0;

        for(int i=1;i<track.getTrackPoints().size()-1 ;i++) {
            double dist =dist2pt(track.getTrackPoints().get(i-1),track.getTrackPoints().get(i));
            if (dist>track.getTrackPoints().get(i-1).getAcc()){
                disttotal=disttotal+dist;
            }
        }
        return disttotal;
    }
}
