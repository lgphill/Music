package com.autopai.music.player;

public abstract class AbstractPlayer implements IPlayer{
    private OnSeekCompleteListener mOnSeekCompleteListener;
    private OnPreparedListener mOnPreparedListener;
    private OnInfoListener mOnInfoListener;
    private OnCompletionListener mOnCompletionListener;
    private OnErrorListener mOnErrorListener;
    private OnBufferingUpdateListener mOnBufferingUpdateListener;

    public AbstractPlayer() {
    }

    protected boolean notifiyInfoChanged(int what, int extra) {
        return mOnInfoListener != null && mOnInfoListener.onInfo(this, what, extra);
    }

    protected boolean notifiyError(int what, int extra) {
        return mOnErrorListener != null && mOnErrorListener.onError(this, what, extra);
    }

    protected void notifiySeekCompleted() {
        if (mOnSeekCompleteListener != null) {
            mOnSeekCompleteListener.onSeekComplete(this);
        }
    }

    protected void notifiyBufferingChanged(int percent) {
        if (mOnBufferingUpdateListener != null) {
            mOnBufferingUpdateListener.onBufferingUpdate(this, percent);
        }
    }

    protected void notifiyCompleted() {
        if (mOnCompletionListener != null) {
            mOnCompletionListener.onCompletion(this);
        }
    }

    protected void notifiyPrepared() {
        if (mOnPreparedListener != null) {
            mOnPreparedListener.onPrepared(this);
        }
    }

    @Override
    public void setOnPreparedListener(IPlayer.OnPreparedListener listener) {
        mOnPreparedListener = listener;
    }

    @Override
    public void setOnCompletionListener(IPlayer.OnCompletionListener listener) {
        mOnCompletionListener = listener;
    }

    @Override
    public void setOnBufferingUpdateListener(IPlayer.OnBufferingUpdateListener listener) {
        mOnBufferingUpdateListener = listener;
    }

    @Override
    public void setOnSeekCompleteListener(IPlayer.OnSeekCompleteListener listener) {
        mOnSeekCompleteListener = listener;
    }

    @Override
    public void setOnErrorListener(IPlayer.OnErrorListener listener) {
        mOnErrorListener = listener;
    }

    @Override
    public void setOnInfoListener(IPlayer.OnInfoListener listener) {
        mOnInfoListener = listener;
    }

}
