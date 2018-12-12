package com.autopai.music.scan;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import com.autopai.music.bean.MusicInfo;
import com.autopai.music.database.MusicProvider;
import com.autopai.music.executor.Task;
import com.autopai.music.executor.ThreadPoolManager;
import com.autopai.music.utils.LogUtil;
import com.autopai.music.utils.MediaDBUtil;
import com.autopai.music.utils.MusicUtil;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

public class ScanService extends Service {
    private static final String TAG = "ScanService";
    private static final int MSG_SCAN = 0;
    private HandlerThread mHandlerThread;
    private Handler mMyHandler;
    private String mPath = Environment.getExternalStorageDirectory() + "/Music";
    @TargetApi(21)
    private ConcurrentLinkedDeque<String> mFolderDeque = new ConcurrentLinkedDeque<>();
    @TargetApi(21)
    private ConcurrentLinkedDeque<ContentValues> mMediaDueue = new ConcurrentLinkedDeque<>();
    private AtomicBoolean mFolderScanFinish = new AtomicBoolean(false);
    private AtomicBoolean mMusicScanFinish = new AtomicBoolean(false);

    @Override
    public void onCreate() {
        super.onCreate();
        mHandlerThread = new HandlerThread("scanThread");
        mHandlerThread.start();
        mMyHandler = new MyHandler(mHandlerThread.getLooper(), this);
        mMyHandler.sendEmptyMessage(MSG_SCAN);
    }

    static class MyHandler extends Handler {
        private WeakReference<ScanService> mReference;
        public MyHandler(Looper looper, ScanService scanService) {
            super(looper);
            mReference = new WeakReference<>(scanService);
        }

        @Override
        public void handleMessage(Message msg) {
            ScanService scanService = mReference.get();
            switch (msg.what) {
                case MSG_SCAN:
                    scanService.startScan();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startScan() {
        if (mPath == null) {
            return;
        }
        LogUtil.i(TAG,"startScan");
        startScanFolder(mPath);
        startScanMediaFromFile();
        startAddMediaToDataBase();
    }

    private void startScanFolder(String path) {
        if (path == null) {
            LogUtil.i(TAG,"startScanFolder path=null");
            return;
        }
        if (new File(path).isDirectory()) {
            mFolderDeque.addLast(path);
        }
        File[] files = new File(path).listFiles();
        for(File file : files) {
            if (file.isDirectory()) {
                mFolderDeque.addLast(file.getAbsolutePath());
                startScanFolder(file.getAbsolutePath());
            }
        }
        LogUtil.i(TAG, "startScanFolder end");
        mFolderScanFinish.set(true);
    }

    private boolean isMusic(File file) {
        if (MusicUtil.isMusic(file.getAbsolutePath())) {
            return true;
        } else {
            return false;
        }
    }

    private void startScanMediaFromFile() {
        Task task = new Task(null) {
            @Override
            public Object executeTask() {
                LogUtil.i(TAG, "startScanMediaFromFile begin");
                while(!mFolderScanFinish.get() || mFolderDeque.size() > 0) {
                    if (mFolderDeque.size() > 0) {
                        String folder = mFolderDeque.pollFirst();
                        File[] files = new File(folder).listFiles();
                        for(File file : files) {
                            if (!file.isDirectory()) {
                                if (isMusic(file)) {
                                    MusicInfo info = MediaDBUtil.getInfoFromAudioDatabase(ScanService.this, file.getAbsolutePath());
                                    if (info.title == null) {
                                        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                                        try {
                                            mediaMetadataRetriever.setDataSource(file.getAbsolutePath());
                                        } catch (Exception e) {
                                            LogUtil.i(TAG, "e=" + e.toString());
                                            LogUtil.i(TAG, "file.path=" + file.getAbsolutePath());
                                            continue;
                                        }
                                        ContentValues contentValues = new ContentValues();
                                        contentValues.put(MusicProvider.MusicEntry.COLUMN_NAME_ISONLINE, 0);
                                        contentValues.put(MusicProvider.MusicEntry.COLUMN_NAME_URL, file.getAbsolutePath());
                                        contentValues.put(MusicProvider.MusicEntry.COLUMN_NAME_TITLE,
                                                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
                                        contentValues.put(MusicProvider.MusicEntry.COLUMN_NAME_ARTIST,
                                                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
                                        mMediaDueue.addLast(contentValues);
                                    }else {
                                        ContentValues contentValues = new ContentValues();
                                        contentValues.put(MusicProvider.MusicEntry.COLUMN_NAME_ISONLINE, 0);
                                        contentValues.put(MusicProvider.MusicEntry.COLUMN_NAME_URL, file.getAbsolutePath());
                                        contentValues.put(MusicProvider.MusicEntry.COLUMN_NAME_TITLE,info.title);
                                        contentValues.put(MusicProvider.MusicEntry.COLUMN_NAME_ARTIST,info.artist);
                                        mMediaDueue.addLast(contentValues);
                                    }
                                }
                            }
                        }
                    } else {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                mMusicScanFinish.set(true);
                LogUtil.i(TAG, "startScanMediaFromFile end");
                return null;
            }
        };
        ThreadPoolManager.getInstance().executeForegroundTask(task);
    }

    private void startAddMediaToDataBase() {
        Task task = new Task(null) {
            @Override
            public Object executeTask() {
                LogUtil.i(TAG, "startAddMediaToDataBase begin");
                while(!mMusicScanFinish.get() || mMediaDueue.size() > 0) {
                    if (mMediaDueue.size() > 0) {
                        ContentValues contentValues = mMediaDueue.pollFirst();
                        ContentResolver contentResolver = getContentResolver();
                        contentResolver.insert(Uri.withAppendedPath(MusicProvider.AUTHORITY_URI, MusicProvider.MusicEntry.TABLE_NAME), contentValues);
                    } else {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                LogUtil.i(TAG, "startAddMediaToDataBase end");
                return null;
            }
        };
        ThreadPoolManager.getInstance().executeForegroundTask(task);
    }
}
