package fr.kriket.oso.view.activity;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.util.Log;

import fr.kriket.oso.R;
import fr.kriket.oso.tools.NumberPickerPreference;


/**
 * Created by fred on 1/15/17.
 */

public class preferenceActivity extends PreferenceActivity {
    private static final String TAG = "preferenceActivity";

    /**
     * Called when the activity is first created.
     */


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

    }
}






