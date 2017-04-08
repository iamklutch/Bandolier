package com.yukidev.bandolier.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.yukidev.bandolier.R;

/**
 * Created by James on 9/18/2015.
 */
public class SettingsFragment extends PreferenceFragment {

    public static final String KEY_EMAIL = "pref_email_edittext";
    public static final String KEY_EMAIL_CHECK = "pref_email_checkbox";
    private SharedPreferences mPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Boolean check = mPreferences.getBoolean(KEY_EMAIL_CHECK, false);
        String value = mPreferences.getString(KEY_EMAIL, "No Stored Email");
        Preference preference = findPreference(KEY_EMAIL);
        preference.setSummary(value);

        mListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                        if (key.equals(KEY_EMAIL_CHECK)){

                        }else{
                            String value = sharedPreferences.getString(key, "");
                            Preference preference = findPreference(key);
                            preference.setSummary(value);
                        }


                    }

                };
        mPreferences.registerOnSharedPreferenceChangeListener(mListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPreferences.unregisterOnSharedPreferenceChangeListener(mListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPreferences.registerOnSharedPreferenceChangeListener(mListener);
    }
}