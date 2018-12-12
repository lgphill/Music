package com.autopai.music.utils;

import android.util.Log;

public class LogUtil {
    public static final String APP_LOG = "music_";
    private static final boolean IS_DEBUG = true;
    public static void i(String tag, String content) {
        if (IS_DEBUG) {
            Log.i(APP_LOG + tag, content);
        }
    }

    public static void e(String tag, String content) {
        if (IS_DEBUG) {
            Log.e(APP_LOG + tag, content);
        }
    }
}
