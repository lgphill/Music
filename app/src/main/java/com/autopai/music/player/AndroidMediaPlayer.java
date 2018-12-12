package com.autopai.music.player;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Map;

public class AndroidMediaPlayer extends AbstractPlayer {
    private MediaPlayer mMediaPlayer;
    private AndroidMediaPlayerListenerHolder mAndroidMediaPlayerListenerHolder;

    public AndroidMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mAndroidMediaPlayerListenerHolder = new AndroidMediaPlayerListenerHolder(this);
        bindListener();
    }

    @Override
    public void setDataSource(Context context, Uri uri) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        mMediaPlayer.setDataSource(context, uri);
    }

    @Override
    public void setDataSource(Context context, Uri uri, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        mMediaPlayer.setDataSource(context, uri, headers);
    }

    @Override
    public void setDataSource(FileDescriptor fd) throws IOException, IllegalArgumentException, IllegalStateException {
        mMediaPlayer.setDataSource(fd);
    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        mMediaPlayer.setDataSource(path);
    }

    @Override
    public void prepareAsync() throws IllegalStateException {
        mMediaPlayer.prepareAsync();
    }

    @Override
    public void start() throws IllegalStateException {
        mMediaPlayer.start();
    }

    @Override
    public void stop() throws IllegalStateException {
        mMediaPlayer.stop();
    }

    @Override
    public void pause() throws IllegalStateException {
        mMediaPlayer.pause();
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    @Override
    public void seekTo(long msec) throws IllegalStateException {
        mMediaPlayer.seekTo((int)msec);
    }

    @Override
    public long getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return mMediaPlayer.getDuration();
    }

    @Override
    public void release() {
        mMediaPlayer.release();
    }

    @Override
    public void reset() {
        mMediaPlayer.reset();
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        mMediaPlayer.setVolume(leftVolume, rightVolume);
    }

    @Override
    public int getAudioSessionId() {
        return mMediaPlayer.getAudioSessionId();
    }

    @Override
    public void setAudioStreamType(int streamtype) {

    }

    @Override
    public void setLooping(boolean looping) {

    }

    @Override
    public boolean isLooping() {
        return false;
    }

    private void bindListener() {
        mMediaPlayer.setOnPreparedListener(mAndroidMediaPlayerListenerHolder);
        mMediaPlayer.setOnCompletionListener(mAndroidMediaPlayerListenerHolder);
        mMediaPlayer.setOnErrorListener(mAndroidMediaPlayerListenerHolder);
        mMediaPlayer.setOnInfoListener(mAndroidMediaPlayerListenerHolder);
        mMediaPlayer.setOnBufferingUpdateListener(mAndroidMediaPlayerListenerHolder);
        mMediaPlayer.setOnSeekCompleteListener(mAndroidMediaPlayerListenerHolder);
    }

    private class AndroidMediaPlayerListenerHolder implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener,
            MediaPlayer.OnInfoListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener{
        private WeakReference<AndroidMediaPlayer> mWeakReference;
        public AndroidMediaPlayerListenerHolder(AndroidMediaPlayer androidMediaPlayer) {
            mWeakReference = new WeakReference<>(androidMediaPlayer);
        }
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            if (mWeakReference.get() != null) {
                notifiyBufferingChanged(percent);
            }
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            if (mWeakReference.get() != null) {
                notifiyCompleted();
            }
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            if (mWeakReference.get() != null) {
                return notifiyError(what, extra);
            }
            return false;
        }

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            if (mWeakReference.get() != null) {
                notifiyInfoChanged(what, extra);
            }
            return false;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            if (mWeakReference.get() != null) {
                notifiyPrepared();
            }
        }

        @Override
        public void onSeekComplete(MediaPlayer mp) {
            if (mWeakReference.get() != null) {
                notifiySeekCompleted();
            }
        }
    }
}
