package com.autopai.music.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;

import com.autopai.music.bean.MusicInfo;

import java.util.ArrayList;

public class MediaDBUtil {
    public static MusicInfo getInfoFromAudioDatabase(Context context, String filePath) {
        MusicInfo res = new MusicInfo();
        Cursor c = null;
        try {
            String[] mediaColumns = { MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.SIZE};
            c = CursorUtils.query(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    mediaColumns, MediaStore.Audio.Media.DATA +" = \""+ filePath +"\"", null, null);
            if(c != null){
                if (c.moveToFirst()) {
                    res.artist = c.getString(c
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    res.title = c.getString(c
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    res.id = c.getLong(c
                            .getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                }
                c.close();
            }
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if(c != null)
                c.close();
        }
        return res;
    }
    public static int getDurationFromAudioDatabase(Context context,String filePath) {
        int res = 0;
        Cursor c = null;
        try {
            String[] mediaColumns = { MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DURATION };
            c = CursorUtils.query(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    mediaColumns, MediaStore.Audio.Media.DATA +" = \""+ filePath +"\"", null, null);
            if(c != null){
                if (c.moveToFirst()) {
                    {
                        res = c.getInt(c
                                .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));  //MediaStoreint.Audio.Media.TITLE
                    }
                }
                c.close();
            }
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if(c != null)
                c.close();
        }
        return res;
    }

    public static String getInfoFromAudioDatabase(Context context,String fieldName,String fileName) {
        String s = null;
        Cursor c = null;
        try {
            String[] mediaColumns = { MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST };
            c = CursorUtils.query(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    mediaColumns, MediaStore.Audio.Media.DATA +" = \""+ fileName +"\"", null, null);
            if(c != null){
                if (c.moveToFirst()) {
                    {
                        s = c.getString(c
                                .getColumnIndexOrThrow(fieldName));  //MediaStore.Audio.Media.TITLE
                    }
                }
                c.close();
            }
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if(c != null)
                c.close();
        }
        return s;
    }

    public static void scanFileForMediaScanner(Context context, String path) {
        Bundle args = new Bundle();
        args.putString("filepath", path);
        Intent intent = new Intent();
        intent.setClassName("com.android.providers.media", "com.android.providers.media.MediaScannerService");
        intent.putExtras(args);
        try {
            context.startService(intent);
        } catch (Exception e) {
            e.getCause();
            e.printStackTrace();
        }
    }

    public static int getMusicFileCount(Context context,long minSize){
        int res =0;

        String s = null;
        Cursor c = null;
        try {
            String[] mediaColumns = { MediaStore.Audio.Media._ID };
            c = CursorUtils.query(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    mediaColumns, MediaStore.Audio.Media.IS_MUSIC +" = \"1\" AND "+MediaStore.Audio.Media.SIZE +" > "+minSize, null, null);
            if(c != null){
                res = c.getCount();
            }
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if(c != null)
                c.close();
        }

        return res;
    }

    public static ArrayList<String> getAllFolders(Context context){
        ArrayList<String> res = new  ArrayList<String>();

        String s = null;
        Cursor c = null;
        try {
            String[] cols = new String[] {"_id", "_data"};
            c = CursorUtils.query(context, MediaStore.Files.getContentUri("external"),
                    cols, "_size ==0) group by (_data", null, null);
            if(c != null){
                c.moveToFirst();
                for(int i = 0;i<c.getCount();i++){
                    String folderName = c.getString(c
                            .getColumnIndexOrThrow("_data"));
                    res.add(folderName);
                    c.moveToNext();
                }
            }
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if(c != null)
                c.close();
        }
        return res;
    }

    public static ArrayList<String> getFileListNewAdded(Context context, String data_added){
        ArrayList<String> res = new  ArrayList<String>();
        Cursor c = null;
        try {
            String[] cols = new String[] {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA};
            c = CursorUtils.query(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    cols, MediaStore.Audio.Media.IS_MUSIC +" = \"1\" AND "+MediaStore.Audio.Media.DATE_ADDED +" > "+data_added, null, null);
            if(c != null){
                c.moveToFirst();
                for(int i = 0;i<c.getCount();i++){
                    String s = c.getString(c
                            .getColumnIndexOrThrow("_data"));
                    res.add(s);
                    c.moveToNext();
                }
            }
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if(c != null)
                c.close();
        }
        return res;
    }


    public static String getMaxDateAdded(Context context){
        String res = null;
        Cursor c = null;
        try {
            String[] cols = new String[] {"_id", "max(date_added) as _max"};
            c = CursorUtils.query(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    cols, MediaStore.Audio.Media.IS_MUSIC +" = \"1\" ", null, null);
            if(c != null){
                c.moveToFirst();
                res = c.getString(c
                        .getColumnIndexOrThrow("_max"));
            }
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if(c != null)
                c.close();
        }
        return res;
    }

    public static void addFileToMediaDB(Context context,MusicInfo info){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Media.TITLE, info.title);
        values.put(MediaStore.Audio.Media.ARTIST, info.artist);
        values.put(MediaStore.MediaColumns.DATA, info.path);
        values.put(MediaStore.Audio.Media.IS_MUSIC, true);
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                values);
    }
}
