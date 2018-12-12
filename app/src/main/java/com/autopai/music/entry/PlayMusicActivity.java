package com.autopai.music.entry;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;

import com.autopai.music.bean.MusicInfo;
import com.autopai.music.R;
import com.autopai.music.player.IPlayer;
import com.autopai.music.player.PlayerFactory;
import com.autopai.music.utils.LogUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

public class PlayMusicActivity extends BaseActivity {
    private static final String TAG = "PlayMusicActivity";
    public static final String PATH = "path";
    private int mPosition;
    private String mPath;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playmusic);
        mPath = getIntent().getStringExtra(PATH);
        mTextView = findViewById(R.id.textview);
        mTextView.setText(mPath);
        mPosition = getIntent().getIntExtra("position", 0);

        if (mPath != null) {
            startServiceToPlay();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    void startServiceToPlay() {
        Intent intent = new Intent();
        intent.setClass(this, MusicPlayService.class);
        intent.putExtra(MusicPlayService.PATH, mPath);
        intent.putExtra(MusicPlayService.COMMAND, MusicPlayService.COMMAND_PLAY);
        startService(intent);
    }

}
