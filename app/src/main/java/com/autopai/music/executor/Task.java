package com.autopai.music.executor;

import android.os.Message;

public abstract class Task implements Runnable {
    private static final String TAG ="Task";
    private Message mMessage;

    public Task(Message msg) {
        mMessage = msg;
    }

    @Override
    public void run() {
        Object object = executeTask();

        if (mMessage != null) {
            mMessage.obj = object;
            mMessage.sendToTarget();
        }
    }

    public abstract Object executeTask();
}
