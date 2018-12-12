package com.autopai.music.entry;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.RemoteViews;
import com.autopai.music.R;
import com.autopai.music.player.IPlayer;
import com.autopai.music.player.PlayerFactory;
import com.autopai.music.utils.LogUtil;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class MusicPlayService extends Service {
    private static final String TAG = "MusicPlayService";
    public static final String PATH = "path";
    public static final String COMMAND = "command";
    public static final String COMMAND_PLAY = "play";
    public static final String STOP_PLAY_ACTION ="com.autopai.music.stop_music";
    public static final String NEXT_PLAY_ACTION ="com.autopai.music.next_music";
    public static final String CANCEL_PLAY_ACTION ="com.autopai.music.cancel_music";
    private static final int MSG_PLAY = 0;
    private static final int MSG_STOP = 1;
    private static final int MSG_NEXT = 2;
    private static final int MSG_CANCEL = 3;
    private String mPath;
    private String mCommand;
    private IPlayer iPlayer;
    private MyHandler mMyHandler;
    private HandlerThread mHandlerThread;
    public static class MyHandler extends Handler {
        private WeakReference<MusicPlayService> mReference;

        public MyHandler(Looper looper) {
            super(looper);
        }
        public void setContext(MusicPlayService playMusicActivity) {
            mReference = new WeakReference<>(playMusicActivity);
        }
        @Override
        public void handleMessage(Message msg) {
            MusicPlayService playMusicActivity = mReference.get();
            switch (msg.what) {
                case MSG_PLAY:
                    if (playMusicActivity != null
                            && playMusicActivity.iPlayer != null) {
                        playMusicActivity.iPlayer.start();
                    }
                    break;
                case MSG_STOP:
                    if ( playMusicActivity!= null
                            && playMusicActivity.iPlayer != null) {
                        if (playMusicActivity.iPlayer.isPlaying()) {
                            playMusicActivity.iPlayer.stop();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    IPlayer.OnPreparedListener mOnPreparedListener = new IPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IPlayer mp) {
            LogUtil.i(TAG, "onPrepared");
            Message msg = mMyHandler.obtainMessage();
            msg.what = MSG_PLAY;
            mMyHandler.sendMessage(msg);
        }
    };

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtil.i(TAG, "mBroadcastReceiver action = " + action);
            if (action == null) {
                return;
            }
            if (action.equals(STOP_PLAY_ACTION)) {
                Message msg = mMyHandler.obtainMessage();
                msg.what = MSG_STOP;
                mMyHandler.sendMessage(msg);
            } else if (action.equals(NEXT_PLAY_ACTION)) {
                Message msg = mMyHandler.obtainMessage();
                msg.what = MSG_NEXT;
                mMyHandler.sendMessage(msg);
            }else if (action.equals(CANCEL_PLAY_ACTION)) {
                Message msg = mMyHandler.obtainMessage();
                msg.what = MSG_CANCEL;
                mMyHandler.sendMessage(msg);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mHandlerThread = new HandlerThread(TAG);
        mHandlerThread.start();
        mMyHandler = new MyHandler(mHandlerThread.getLooper());
        mMyHandler.setContext(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mPath = intent.getStringExtra(PATH);
        mCommand = intent.getStringExtra(COMMAND);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(STOP_PLAY_ACTION);
        registerReceiver(mBroadcastReceiver, intentFilter);

        if (mCommand != null && mCommand.equals(COMMAND_PLAY)) {
            try {
                iPlayer = PlayerFactory.getInstance().getPlayer();
                iPlayer.reset();
                iPlayer.setDataSource(mPath);
                iPlayer.setOnPreparedListener(mOnPreparedListener);
                iPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
            startNotification();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @TargetApi(16)
    void startNotification() {
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.newstatusbar);
        views.setTextViewText(R.id.title, "歌曲名");
        views.setTextViewText(R.id.artist, "歌手名");
        Intent intent;
        PendingIntent pIntent;

        intent = new Intent(STOP_PLAY_ACTION);
        pIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        views.setOnClickPendingIntent(R.id.btn_pause, pIntent);

        intent = new Intent(NEXT_PLAY_ACTION);
        intent.setClass(MusicPlayService.this, MusicPlayService.class);
        pIntent = PendingIntent.getService(MusicPlayService.this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        views.setOnClickPendingIntent(R.id.btn_next, pIntent);

        Notification.Builder mNotifyBuilder = new Notification.Builder(this);
        Notification foregroundNote = mNotifyBuilder.build();
        foregroundNote.contentView = views;
        foregroundNote.flags |= Notification.FLAG_ONGOING_EVENT;
        foregroundNote.icon = R.drawable.ic_launcher_background;
        foregroundNote.contentIntent = PendingIntent.getActivity(MusicPlayService.this, 0, new Intent(
                "com.android.music.PLAYBACK_VIEWER"), PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            startForeground(1, foregroundNote);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
