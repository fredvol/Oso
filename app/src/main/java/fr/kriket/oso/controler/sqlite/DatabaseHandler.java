package fr.kriket.oso.controler.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by fred on 1/1/17.
 */


public class DatabaseHandler extends SQLiteOpenHelper {

    public static final String TRACKPT_KEY = "id";
    public static final String TRACKPT_SESSIONID = "SessionId";
    public static final String TRACKPT_TRACKINGID = "TrackingId";
    public static final String TRACKPT_TIMESTAMP = "TimeStamp";
    public static final String TRACKPT_LAT = "Lat";
    public static final String TRACKPT_LONG = "Long";
    public static final String TRACKPT_ALT = "Alt";
    public static final String TRACKPT_ACC = "Acc";
    public static final String TRACKPT_BAT = "Bat";
    public static final String TRACKPT_NETWORKSTRENGH = "NetworkStrength";
    public static final String TRACKPT_COMMENT = "Comment";
    public static final String TRACKPT_ISSENT = "isSent";


    public static final int DATABASE_VERSION = 2;
    public static final String TRACKPT_TABLE_NAME = "TrackPointTable";

    public static final String TRACKPT_TABLE_CREATE =

            "CREATE TABLE " + TRACKPT_TABLE_NAME + " (" +

                    TRACKPT_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TRACKPT_SESSIONID +" TEXT,"+
                    TRACKPT_TRACKINGID +" TEXT,"+
                    TRACKPT_TIMESTAMP +" INTEGER,"+
                    TRACKPT_LAT+" REAL,"+
                    TRACKPT_LONG+" REAL,"+
                    TRACKPT_ALT+" INTEGER,"+
                    TRACKPT_BAT+" INTEGER,"+
                    TRACKPT_ACC+" REAL,"+
                    TRACKPT_NETWORKSTRENGH+" INTEGER,"+
                    TRACKPT_COMMENT + " TEXT, " +
                    TRACKPT_ISSENT + " INTEGER);";


    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TRACKPT_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d("DatabaseHandler", " onUpgrade");
        if(newVersion > oldVersion){

            String sql = "drop table if exists " + TRACKPT_TABLE_NAME;
            sqLiteDatabase.execSQL(sql);

            onCreate(sqLiteDatabase);
        }

    }


}

