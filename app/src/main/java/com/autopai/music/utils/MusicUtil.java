package com.autopai.music.utils;

public class MusicUtil {
    private static final String[] AUDIO_TYPES = {"mp3", "wav", "aac", "amr"}; // wma is not supported by exoplayer and mediaplayer
    public static boolean isMusic(String format) {
        if (format == null) {
            return false;
        }
        format = format.toLowerCase();
        for(String audioPath : AUDIO_TYPES) {
            if (format.contains(audioPath)) {
                return true;
            }
        }
        return false;
    }

    public static String getFormatName(String fileName) {
        fileName = fileName.trim();
        String s[] = fileName.split("\\.");
        if (s.length >= 2) {
            return s[s.length - 1];
        }
        return "";
    }
}
