package fr.kriket.oso.view.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;

import fr.kriket.oso.R;
import fr.kriket.oso.tools.NumberPickerPreference;

public class MyPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "preferencefragm";
    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        // show the current value in the settings screen
        updatefield();

    }

    public void updatefield() {
        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            pickPreferenceObject(getPreferenceScreen().getPreference(i));
        }

    }


    private void pickPreferenceObject(Preference p) {
        if (p instanceof PreferenceCategory) {
            PreferenceCategory cat = (PreferenceCategory) p;
            for (int i = 0; i < cat.getPreferenceCount(); i++) {
                pickPreferenceObject(cat.getPreference(i));
            }
        } else {
            initSummary(p);
        }
    }
    // TODO: 1/17/17 Display  value on start


    private void initSummary(Preference p) {

        if (p instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            p.setSummary(editTextPref.getText());
        }

        if (p instanceof NumberPickerPreference) {
            NumberPickerPreference numberPickerPreference = (NumberPickerPreference) p;
            p.setSummary("every: "+ numberPickerPreference.getValuestr()+ " min");
        }

        // More logic for ListPreference, etc...
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("username")) {
            Preference pref = findPreference(key);
            pref.setSummary(sharedPreferences.getString(key, ""));
        }

        if (key.equals("log_interval") || key.equals("Tracking_interval")) {
            Preference pref =  findPreference(key);
            pref.setSummary("every: "+sharedPreferences.getInt(key,3)+ " min");    // FIXME: 1/19/17 the default value 3 should not be the same for log and track (display default value)
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

}
