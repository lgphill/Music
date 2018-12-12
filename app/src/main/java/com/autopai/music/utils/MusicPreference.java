package com.autopai.music.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.autopai.music.entry.MusicApp;
import com.autopai.music.R;

public class MusicPreference {
    private volatile static MusicPreference sMusicPreference;
    private SharedPreferences mSharedPreferences;
    private Context mAppContext;
    private MusicPreference() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(MusicApp.getInstance());
        mAppContext = MusicApp.getInstance();
    }

    public static MusicPreference getInstance() {
        if (sMusicPreference == null) {
            synchronized (MusicPreference.class) {
                if (sMusicPreference == null) {
                    sMusicPreference = new MusicPreference();
                }
            }
        }
        return sMusicPreference;
    }

    public int getPlayerType() {
        String key = mAppContext.getString(R.string.pref_key_player);
        String value = mSharedPreferences.getString(key, "");
        try {
            return Integer.valueOf(value).intValue();
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
