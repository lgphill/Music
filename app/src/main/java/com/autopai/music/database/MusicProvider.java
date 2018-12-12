package com.autopai.music.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.autopai.music.utils.LogUtil;

public class MusicProvider extends ContentProvider {
    private static final String TAG = "MusicProvider";
    private static final int MUSIC = 100;
    private static final int MUSIC_ID = 101;
    private static final String AUTHORITY = "com.autopai.music.database.MusicProvider";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
    private static final Uri CONTENT_URI_MUSIC = Uri.withAppendedPath(AUTHORITY_URI, MusicEntry.TABLE_NAME);
    private MusicSQLiteOpenHelper mMusicSQLiteOpenHelper;
    private UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    @Override
    public boolean onCreate() {
        LogUtil.i(TAG, "onCreate");
        mMusicSQLiteOpenHelper = new MusicSQLiteOpenHelper(getContext());
        initUriMatch();
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Cursor query( Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        sqLiteQueryBuilder.setTables(MusicEntry.TABLE_NAME);
        int match = mUriMatcher.match(uri);
        switch (match) {
            case MUSIC:
                break;
            case MUSIC_ID:
                selection = DatabaseUtils.concatenateWhere(selection, "id");
                selectionArgs = DatabaseUtils.appendSelectionArgs(selectionArgs, new String[]{Long.toString(ContentUris.parseId(uri))});
                break;
            default:
                break;
        }
        Cursor cursor = null;
        try {
            cursor = sqLiteQueryBuilder.query(getReadableDatabase(), projection, selection,
                    selectionArgs,null, sortOrder,null);
            if (cursor != null) {
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
            }
        } catch (Exception e) {
            LogUtil.i(TAG,"query error e = " + e.toString());
        }
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = getWriteableDatabase();
        if (db == null) {
            return null;
        }
        int match = mUriMatcher.match(uri);
        long id = -1;
        switch (match) {
            case MUSIC:
                try {
                    id = db.insert(MusicEntry.TABLE_NAME, "", values);
                } catch (Throwable t) {
                    id = -1;
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown insert URI " + uri);
        }
        if (id < 0) {
            LogUtil.i(TAG,"insert id = " + id);
            return null;
        }
        Uri inserted = ContentUris.withAppendedId(uri, id);
        notifyChange(uri);
        return inserted;
    }

    @Override
    public int bulkInsert(@Nullable Uri uri, @Nullable ContentValues[] values) {
        return super.bulkInsert(uri, values);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = getWriteableDatabase();
        if (db == null) {
            return 0;
        }
        int match = mUriMatcher.match(uri);
        int deleted = 0;
        switch (match) {
            case MUSIC_ID: {
                selection = DatabaseUtils.concatenateWhere(selection,
                        MusicEntry.TABLE_NAME + ".id=?");
                selectionArgs = DatabaseUtils.appendSelectionArgs(selectionArgs,
                        new String[] { Long.toString(ContentUris.parseId(uri)) });
                // fall through
                try {
                    deleted = db.delete(MusicEntry.TABLE_NAME, selection, selectionArgs);
                } catch (Throwable t) {
                }
                break;
            }
            case MUSIC:
                try {
                    deleted = db.delete(MusicEntry.TABLE_NAME, selection, selectionArgs);
                } catch (Throwable t) {
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown delete URI " + uri);
        }
        if (deleted > 0) {
            notifyChange(uri);
        }
        return deleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = getWriteableDatabase();
        int count = db.update(MusicEntry.TABLE_NAME, values, selection, selectionArgs);
        if (count > 0) {
            notifyChange(uri);
        }
        return count;
    }

    private void notifyChange(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        contentResolver.notifyChange(uri, null);
    }

    private void initUriMatch() {
        mUriMatcher.addURI(AUTHORITY, MusicEntry.TABLE_NAME, MUSIC);
        mUriMatcher.addURI(AUTHORITY, MusicEntry.TABLE_NAME + "/#", MUSIC_ID);
    }

    private SQLiteDatabase getReadableDatabase() {
        return mMusicSQLiteOpenHelper.getReadableDatabase();
    }

    private SQLiteDatabase getWriteableDatabase() {
        return mMusicSQLiteOpenHelper.getWritableDatabase();
    }

    public static class MusicEntry {
        public static final String TABLE_NAME = "table_music";
        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_ARTIST = "artist";
        public static final String COLUMN_NAME_ISONLINE = "isonline";
    }

    public static class MusicSQLiteOpenHelper extends SQLiteOpenHelper {
        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_NAME = "Music.db";
        private static final String SQL_CREATE_ENTRIES =
                " CREATE TABLE IF NOT EXISTS " + MusicEntry.TABLE_NAME + " (" +
                        MusicEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MusicEntry.COLUMN_NAME_URL + " VARCHAR UNIQUE, " +
                        MusicEntry.COLUMN_NAME_TITLE + " VARCHAR, " +
                        MusicEntry.COLUMN_NAME_ARTIST + " VARCHAR, " +
                        MusicEntry.COLUMN_NAME_ISONLINE + " INTEGER)";

        public MusicSQLiteOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            LogUtil.e(TAG, "MusicSQLiteOpenHelper onCreate");
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
