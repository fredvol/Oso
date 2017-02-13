package fr.kriket.oso.controler.internal;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import fr.kriket.oso.controler.sqlite.DatabaseHandler;
import fr.kriket.oso.model.TrackPoint;

import static fr.kriket.oso.controler.sqlite.DatabaseHandler.DATABASE_VERSION;
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

        //final int VERSION = 2;
        //final String TRACKPT_TABLE_NAME = "TrackPointTable"; // TODO: 2/5/17  Need to be group somewhere

        DatabaseHandler mDbHelper = new DatabaseHandler(mcontext,TRACKPT_TABLE_NAME,null,DATABASE_VERSION);  //TODO: 2/5/17  call uniform method


                // Gets the data repository in write mode

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String strSQL="select * from " + TRACKPT_TABLE_NAME;

        if (sessionID != null) {   //Means is for send
            strSQL=strSQL+" where SessionId= '" +sessionID+"' and isSent=0 order by TimeStamp DESC Limit 100 ";
        }

        Cursor cursor = db.rawQuery(strSQL , null);

        List listTrackPoint = new ArrayList<>();                        // TODO: 2/5/17  need to find a way to not be depend of the colums number
        while(cursor.moveToNext()) {
           // Log.d(TAG,"cursor"+cursor.toString());

            long RowId = cursor.getLong(0);
            String UserId= null;
            String SessionId= cursor.getString(1);
            String TrackingId= cursor.getString(2);
            long timeStamp= cursor.getLong(3);
            double Lati = cursor.getDouble(4);
            double Long = cursor.getDouble(5);
            double Alt = cursor.getDouble(6);
            int Bat = cursor.getInt(7);
            float Acc = cursor.getInt(8);
            int NetworkStrength = cursor.getInt(9);

            String Comment= cursor.getString(10);
            boolean isSent= cursor.getInt(11)> 0;

            Date date = new Date(timeStamp);

            TrackPoint trackPoint = new TrackPoint (RowId, SessionId,TrackingId,date,timeStamp,Lati,Long,Alt,Bat,Acc,NetworkStrength,Comment,isSent);
          //  Log.d(TAG,"trackPoint"+trackPoint.toString());
            listTrackPoint.add(trackPoint);
        }
        cursor.close();

        return listTrackPoint;
    }
}
