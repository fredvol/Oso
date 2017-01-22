package fr.kriket.oso.controler.internal;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.kriket.oso.controler.sqlite.DatabaseHandler;
import fr.kriket.oso.model.TrackPoint;

import static fr.kriket.oso.controler.sqlite.DatabaseHandler.TRACKPT_TABLE_NAME;

/**
 * Created by fred on 1/7/17.
 */

public class GetTrackBookLoaderController  {

    private static final String TAG = "GetTrackBookLderCntrllr";

    /** Constructeur privé */
    private GetTrackBookLoaderController()
    {}

    /** Instance unique non préinitialisée */
    private static GetTrackBookLoaderController INSTANCE = null;

    /** Point d'accès pour l'instance unique du singleton */
    public static synchronized GetTrackBookLoaderController getInstance()
    {
        if (INSTANCE == null)
        { 	INSTANCE = new GetTrackBookLoaderController();
        }
        return INSTANCE;
    }


    /**
     * @param mcontext
     * @param sessionID ( if null return all sessionId
     * @return a List of Trackpoint
     */
    public List getPointsBySeesionId(Context mcontext, String sessionID) {

        DatabaseHandler mDbHelper = new DatabaseHandler(mcontext,TRACKPT_TABLE_NAME,null,1);


        final String TRACKPT_TABLE_NAME = "TrackPointTable";

        // Gets the data repository in write mode

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String strSQL="select * from " + TRACKPT_TABLE_NAME;

        if (sessionID != null) {   //Means is for send
            strSQL=strSQL+" where SessionId= '" +sessionID+"' and isSent=0 order by TimeStamp DESC Limit 100 ";
        }

        Cursor cursor = db.rawQuery(strSQL , null);


        List listTrackPoint = new ArrayList<>();
        while(cursor.moveToNext()) {
            Log.d(TAG,"cursor"+cursor.toString());

            long RowId = cursor.getLong(0);
            String UserId= null;
            String SessionId= cursor.getString(1);

            long timeStamp= cursor.getLong(2);
            double Lati = cursor.getDouble(3);
            double Long = cursor.getDouble(4);
            double Alt = cursor.getDouble(5);
            int Bat = cursor.getInt(6);
            int NetworkStrength = cursor.getInt(7);
            String Comment= cursor.getString(8);
            boolean isSent= cursor.getInt(8)> 0;

            Date date = new Date(timeStamp);


            TrackPoint trackPoint = new TrackPoint (RowId, UserId, SessionId,date,timeStamp,Lati,Long,Alt,Bat,NetworkStrength,Comment,isSent);
            Log.d(TAG,"trackPoint"+trackPoint.toString());
            listTrackPoint.add(trackPoint);
        }
        cursor.close();

        return listTrackPoint;
    }
}
