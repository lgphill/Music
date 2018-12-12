package com.autopai.music.entry;

import android.app.Application;

import com.autopai.music.utils.LogUtil;

public class MusicApp extends Application {
    private static final String TAG = "MusicApp";
    private static MusicApp mMusicApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mMusicApp = this;
        LogUtil.i(TAG,"onCreate");
    }

    public static MusicApp getInstance() {
        return mMusicApp;
    }
}
