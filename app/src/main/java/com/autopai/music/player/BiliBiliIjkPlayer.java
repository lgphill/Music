package com.autopai.music.player;

import android.content.Context;
import android.net.Uri;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Map;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class BiliBiliIjkPlayer extends AbstractPlayer {
    private IjkMediaPlayer mIjkMediaPlayer;

    public BiliBiliIjkPlayer() {
        mIjkMediaPlayer = new IjkMediaPlayer();
    }

    @Override
    public void setDataSource(Context context, Uri uri) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        mIjkMediaPlayer.setDataSource(context, uri);
    }

    @Override
    public void setDataSource(Context context, Uri uri, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        mIjkMediaPlayer.setDataSource(context, uri, headers);
    }

    @Override
    public void setDataSource(FileDescriptor fd) throws IOException, IllegalArgumentException, IllegalStateException {
        mIjkMediaPlayer.setDataSource(fd);
    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        mIjkMediaPlayer.setDataSource(path);
    }

    @Override
    public void prepareAsync() throws IllegalStateException {
        mIjkMediaPlayer.prepareAsync();
    }

    @Override
    public void start() throws IllegalStateException {
        mIjkMediaPlayer.start();
    }

    @Override
    public void stop() throws IllegalStateException {
        mIjkMediaPlayer.stop();
    }

    @Override
    public void pause() throws IllegalStateException {
        mIjkMediaPlayer.pause();
    }

    @Override
    public boolean isPlaying() {
        return mIjkMediaPlayer.isPlaying();
    }

    @Override
    public void seekTo(long msec) throws IllegalStateException {
        mIjkMediaPlayer.seekTo(msec);
    }

    @Override
    public long getCurrentPosition() {
        return mIjkMediaPlayer.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return mIjkMediaPlayer.getDuration();
    }

    @Override
    public void release() {
        mIjkMediaPlayer.release();
    }

    @Override
    public void reset() {
        mIjkMediaPlayer.reset();
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        mIjkMediaPlayer.setVolume(leftVolume, rightVolume);
    }

    @Override
    public int getAudioSessionId() {
        return mIjkMediaPlayer.getAudioSessionId();
    }

    @Override
    public void setAudioStreamType(int streamtype) {
        mIjkMediaPlayer.setAudioStreamType(streamtype);
    }

    @Override
    public void setLooping(boolean looping) {
        mIjkMediaPlayer.setLooping(looping);
    }

    @Override
    public boolean isLooping() {
        return mIjkMediaPlayer.isLooping();
    }
}
