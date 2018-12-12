package com.autopai.music.entry;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.autopai.music.R;
import com.autopai.music.database.MusicProvider;
import com.autopai.music.utils.LogUtil;
import com.autopai.music.utils.MusicUtil;

import java.lang.ref.WeakReference;

public class NativeSongActivity extends BaseActivity {
    private static final String TAG = "NativeSongActivity";
    private ListView mListView;
    private MyAsyncTask mMyAsyncTask;
    private MyCursorAdapter mMyCursorAdapter;
    private String[] projection = new String[] {
        MusicProvider.MusicEntry.COLUMN_NAME_ID,
        MusicProvider.MusicEntry.COLUMN_NAME_ARTIST,
        MusicProvider.MusicEntry.COLUMN_NAME_TITLE,
        MusicProvider.MusicEntry.COLUMN_NAME_URL,
    };
    private static final int COLUMN_ID = 0;
    private static final int COLUMN_ARTIST = 1;
    private static final int COLUMN_TITLE = 2;
    private static final int COLUMN_URL = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native);

        initListView();
        startAsyncTask();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void startAsyncTask() {
        mMyAsyncTask = new MyAsyncTask(this);
        mMyAsyncTask.execute();
    }

    private void initListView() {
        mListView = (ListView) findViewById(R.id.listview);
    }

    public void updateCurrentMusicInfoList() {
//        MusicInfoCacheSingleton.getInstance().setCurrentMusicInfoList(getFilelist());
    }

    public static class MyAsyncTask extends AsyncTask<Object, Object, Cursor> {
        private WeakReference<NativeSongActivity> mReference;

        public MyAsyncTask(NativeSongActivity nativeSongActivity) {
            mReference = new WeakReference<>(nativeSongActivity);
        }

        @Override
        protected Cursor doInBackground(Object... objects) {
            NativeSongActivity nativeSongActivity = mReference.get();
            ContentResolver contentResolver = nativeSongActivity.getContentResolver();
            Uri uri = Uri.withAppendedPath(MusicProvider.AUTHORITY_URI, MusicProvider.MusicEntry.TABLE_NAME);
            return contentResolver.query(uri, nativeSongActivity.projection, null, null, null);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            NativeSongActivity nativeSongActivity = mReference.get();
            nativeSongActivity.initCursorAdapter(cursor);
        }
    }

    private void initCursorAdapter(Cursor cursor) {
        if (cursor == null) {
            return;
        }
        mMyCursorAdapter = new MyCursorAdapter(this, cursor);
        LogUtil.i(TAG, "cursor.getCount = " + cursor.getCount());
        if (mListView != null) {
            mListView.setAdapter(mMyCursorAdapter);
        }
    }

    private class MyCursorAdapter extends CursorAdapter {
        private class ViewHolder {
            TextView mTitleTV;
            TextView mArtitTV;
            TextView mTypeTV;
            LinearLayout mLinearLayout;
        }

        public MyCursorAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.adapter_native, null);
            ViewHolder holder = new ViewHolder();
            holder.mTitleTV = view.findViewById(R.id.title);
            holder.mArtitTV = view.findViewById(R.id.artist);
            holder.mTypeTV = view.findViewById(R.id.type);
            holder.mLinearLayout = view.findViewById(R.id.layout);
            view.setTag(holder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            viewHolder.mTitleTV.setText(cursor.getString(COLUMN_TITLE));
            viewHolder.mArtitTV.setText(cursor.getString(COLUMN_ARTIST));
            String path = cursor.getString(COLUMN_URL);
            String type = MusicUtil.getFormatName(path);
            viewHolder.mTypeTV.setText(type);
            viewHolder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(MusicApp.getInstance(),PlayMusicActivity.class);
                    LogUtil.i(TAG,"NativeSongActivity onClick path=" + path);
                    intent.putExtra(PlayMusicActivity.PATH, path);
                    startActivity(intent);
                }
            });
        }
    }
}
