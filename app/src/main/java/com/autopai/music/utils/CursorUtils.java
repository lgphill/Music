package com.autopai.music.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class CursorUtils {

    public static void closeCursor(Cursor c) {
        if (c != null) {
            c.close();
        }
        c = null;
    }

    public static Cursor query(Context context, Uri uri, String[] projection, String selection,
                               String[] selectionArgs, String sortOrder) {
        return query(context, uri, projection, selection, selectionArgs, sortOrder, 0);
    }

    private static Cursor query(Context context, Uri uri, String[] projection, String selection,
                                String[] selectionArgs, String sortOrder, int limit) {
        Cursor cursor = null;
        try {
            ContentResolver resolver = context.getContentResolver();
            if (resolver == null) {
                return null;
            }
            if (limit > 0) {
                uri = uri.buildUpon().appendQueryParameter("limit", "" + limit).build();
            }
            cursor = resolver.query(uri, projection, selection, selectionArgs, sortOrder);
            return cursor;
        } catch (Exception e) {
            // TODO: handle exception
            e.getCause();
            e.printStackTrace();
            return null;
        }

    }

}
