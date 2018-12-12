package com.autopai.music.player;

import com.autopai.music.utils.LogUtil;
import com.autopai.music.utils.MusicPreference;

public class PlayerFactory {
    private final static String TAG = "PlayerFactory";
    private static volatile PlayerFactory sPlayerFactory;
    public static final int PLAYER_TYPE_AUTO = 0;
    public static final int PLAYER_TYPE_ANDROID_MEDIA = 1;
    public static final int PLAYER_TYPE_BILIBILI_IJK = 2;
    public static final int PLAYER_TYPE_GOOGLE_EXO = 3;
    private int mPlayerType = PLAYER_TYPE_AUTO;
    private GoogleExoPlayer mGoogleExoPlayer;
    private BiliBiliIjkPlayer mBiliBiliIjkPlayer;
    private AndroidMediaPlayer mAndroidMediaPlayer;
    private PlayerFactory() {
    }

    public static PlayerFactory getInstance() {
        if (sPlayerFactory == null) {
            synchronized (PlayerFactory.class) {
                if (sPlayerFactory == null) {
                    sPlayerFactory = new PlayerFactory();
                }
            }
        }
        return sPlayerFactory;
    }

    public IPlayer getPlayer() {
        mPlayerType = MusicPreference.getInstance().getPlayerType();
        LogUtil.i(TAG, "getPlayer mPlayerType=" + mPlayerType);
        IPlayer iPlayer;
        switch (mPlayerType) {
            case PLAYER_TYPE_BILIBILI_IJK:
                if (mBiliBiliIjkPlayer == null) {
                    mBiliBiliIjkPlayer = new BiliBiliIjkPlayer();
                }
                iPlayer = mBiliBiliIjkPlayer;
                break;
            case PLAYER_TYPE_GOOGLE_EXO:
                if (mGoogleExoPlayer == null) {
                    mGoogleExoPlayer = new GoogleExoPlayer();
                }
                iPlayer = mGoogleExoPlayer;
                break;
            case PLAYER_TYPE_AUTO:
            case PLAYER_TYPE_ANDROID_MEDIA:
            default:
                if (mAndroidMediaPlayer == null) {
                    mAndroidMediaPlayer = new AndroidMediaPlayer();
                }
                iPlayer = mAndroidMediaPlayer;
                break;
        }
        return iPlayer;
    }
}
