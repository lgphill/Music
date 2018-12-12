package com.autopai.music.player;

import android.content.Context;
import android.net.Uri;

import com.autopai.music.entry.MusicApp;
import com.autopai.music.utils.LogUtil;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.RandomTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Map;

public class GoogleExoPlayer extends AbstractPlayer {
    private static final String TAG ="GoogleExoPlayer";
    private static final String DOWNLOAD_CONTENT_DIRECTORY = "downloads";
    private SimpleExoPlayer mSimpleExoPlayer;
    private MediaSource mMediaSource;
    private String mUserAgent;
    private Cache mDownloadCache;
    private File downloadDirectory;

    public GoogleExoPlayer() {
        initializePlayer();
    }

    private void initializePlayer() {
        mUserAgent = Util.getUserAgent(MusicApp.getInstance(), "GoogleExoPlayer");
        @DefaultRenderersFactory.ExtensionRendererMode int extensionRendererMode =
                DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;
        DefaultRenderersFactory renderersFactory =
                new DefaultRenderersFactory(MusicApp.getInstance(), extensionRendererMode);
        TrackSelection.Factory trackSelectionFactory = new RandomTrackSelection.Factory();
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(trackSelectionFactory);
        DefaultTrackSelector.Parameters trackSelectorParameters = new DefaultTrackSelector.ParametersBuilder().build();
        trackSelector.setParameters(trackSelectorParameters);
        mSimpleExoPlayer =
                ExoPlayerFactory.newSimpleInstance(
                        MusicApp.getInstance(), renderersFactory, trackSelector);
        mSimpleExoPlayer.addListener(new EventListenerImpl());
    }

    public DataSource.Factory buildDataSourceFactory() {
        DefaultDataSourceFactory upstreamFactory =
                new DefaultDataSourceFactory(MusicApp.getInstance(), buildHttpDataSourceFactory());
        return buildReadOnlyCacheDataSource(upstreamFactory, getDownloadCache());
    }

    public HttpDataSource.Factory buildHttpDataSourceFactory() {
        return new DefaultHttpDataSourceFactory(mUserAgent);
    }

    private synchronized Cache getDownloadCache() {
        if (mDownloadCache == null) {
            File downloadContentDirectory = new File(getDownloadDirectory(), DOWNLOAD_CONTENT_DIRECTORY);
            mDownloadCache = new SimpleCache(downloadContentDirectory, new NoOpCacheEvictor());
        }
        return mDownloadCache;
    }

    private File getDownloadDirectory() {
        if (downloadDirectory == null) {
            downloadDirectory = MusicApp.getInstance().getExternalFilesDir(null);
            if (downloadDirectory == null) {
                downloadDirectory = MusicApp.getInstance().getFilesDir();
            }
        }
        return downloadDirectory;
    }

    private CacheDataSourceFactory buildReadOnlyCacheDataSource(
            DefaultDataSourceFactory upstreamFactory, Cache cache) {
        return new CacheDataSourceFactory(
                cache,
                upstreamFactory,
                new FileDataSourceFactory(),
                /* cacheWriteDataSinkFactory= */ null,
                CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR,
                /* eventListener= */ null);
    }

    @Override
    public void setDataSource(Context context, Uri uri) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        mMediaSource = new ExtractorMediaSource.Factory(buildDataSourceFactory()).createMediaSource(uri);
    }

    @Override
    public void setDataSource(Context context, Uri uri, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        mMediaSource = new ExtractorMediaSource.Factory(buildDataSourceFactory()).createMediaSource(uri);
    }

    @Override
    public void setDataSource(FileDescriptor fd) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException("no support");
    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        mMediaSource = new ExtractorMediaSource.Factory(buildDataSourceFactory()).createMediaSource(Uri.parse(path));
    }

    @Override
    public void prepareAsync() throws IllegalStateException {
        mSimpleExoPlayer.prepare(mMediaSource);
        mSimpleExoPlayer.setPlayWhenReady(false);
    }

    @Override
    public void start() throws IllegalStateException {
        mSimpleExoPlayer.setPlayWhenReady(true);
    }

    @Override
    public void stop() throws IllegalStateException {
        mSimpleExoPlayer.stop();
    }

    @Override
    public void pause() throws IllegalStateException {
        mSimpleExoPlayer.setPlayWhenReady(false);
    }

    @Override
    public boolean isPlaying() {
        if (mSimpleExoPlayer == null)
            return false;
        int state = mSimpleExoPlayer.getPlaybackState();
        switch (state) {
            case ExoPlayer.STATE_BUFFERING:
            case ExoPlayer.STATE_READY:
                return mSimpleExoPlayer.getPlayWhenReady();
            case ExoPlayer.STATE_IDLE:
            case ExoPlayer.STATE_ENDED:
            default:
                return false;
        }
    }

    @Override
    public void seekTo(long msec) throws IllegalStateException {
        mSimpleExoPlayer.seekTo(msec);
    }

    @Override
    public long getCurrentPosition() {
        return mSimpleExoPlayer.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return mSimpleExoPlayer.getDuration();
    }

    @Override
    public void release() {
        boolean isPlaying = mSimpleExoPlayer.isPlayingAd();
        LogUtil.i(TAG, "release isPlaying=" + isPlaying);
        if (isPlaying) {
            mSimpleExoPlayer.stop();
        }
        mSimpleExoPlayer.release();
        try {
            mDownloadCache.release();
        } catch (Cache.CacheException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reset() {
        // no suppport
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        //no support
    }

    @Override
    public int getAudioSessionId() {
        return mSimpleExoPlayer.getAudioSessionId();
    }

    @Override
    public void setAudioStreamType(int streamtype) {
        mSimpleExoPlayer.setAudioStreamType(streamtype);
    }

    @Override
    public void setLooping(boolean looping) {
        // TODO: no support
    }

    @Override
    public boolean isLooping() {
        // TODO: no support
        return false;
    }

    private class EventListenerImpl implements Player.EventListener {
        private boolean mIsBuffering = false;

        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            LogUtil.i(TAG, "onStateChanged playbackState=" + playbackState);
            if (mIsBuffering) {
                switch (playbackState) {
                    case ExoPlayer.STATE_ENDED:
                    case ExoPlayer.STATE_READY:
                        notifiyInfoChanged(MEDIA_INFO_BUFFERING_END, mSimpleExoPlayer.getBufferedPercentage());
                        mIsBuffering = false;
                        break;
                }
            }

            switch (playbackState) {
                case ExoPlayer.STATE_IDLE:
                    notifiyCompleted();
                    break;
                case ExoPlayer.STATE_BUFFERING:
                    notifiyInfoChanged(MEDIA_INFO_BUFFERING_START, mSimpleExoPlayer.getBufferedPercentage());
                    mIsBuffering = true;
                    break;
                case ExoPlayer.STATE_READY:
                    notifiyPrepared();
                    break;
                case ExoPlayer.STATE_ENDED:
                    notifiyCompleted();
                    break;
                default:
                    break;
            }
        }

        public void onError(Exception e){
            notifiyError(MEDIA_ERROR_UNKNOWN, MEDIA_ERROR_UNKNOWN);
        }

    }
}
