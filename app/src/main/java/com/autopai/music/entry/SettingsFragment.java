package com.autopai.music.entry;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.autopai.music.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    public SettingsFragment() {

    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.settings);
    }
}
