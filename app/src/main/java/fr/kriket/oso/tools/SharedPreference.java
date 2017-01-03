package fr.kriket.oso.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


/**
 * Created by fred on 6/7/16.
 */

public class SharedPreference {


    public static final String PREFS_NAME = "OSO_PREFS";


    public SharedPreference() {
        super();
    }





    ///SAVE

    public void save(Context context, String key, Boolean bool) {
        SharedPreferences settings;
        Editor editor;

        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2

        editor.putBoolean(key, bool); //3

        editor.apply(); //4
    }
    public void save(Context context, String key, String text) {
        SharedPreferences settings;
        Editor editor;

        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2

        editor.putString(key, text); //3

        editor.apply(); //4
    }


    //GET VALUES
    public String getValue(Context context, String key) {
        SharedPreferences settings;
        String text;

        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        text = settings.getString(key, null);
        return text;
    }

    public String getValueAnyway(Context context, String key) {
        SharedPreferences settings;
        String text;

        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        text = settings.getString(key, null);

        return text;
    }

    public Boolean getBoolean(Context context, String key) {
        SharedPreferences settings;
        Boolean bool;

        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        bool = settings.getBoolean(key, false);

        return bool;
    }



    /// CLEAR

    public void clearSharedPreference(Context context) {
        SharedPreferences settings;
        Editor editor;

        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.clear();
        editor.apply();
    }

    public void removeValue(Context context, String key) {
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.remove(key);
        editor.apply();
    }
}
