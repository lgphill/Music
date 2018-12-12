package com.autopai.music.executor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolManager {
    private static ThreadPoolManager sThreadPoolManager = new ThreadPoolManager();
    private Executor mForegroundTaskExecutor = new ThreadPoolExecutor(1, Integer.MAX_VALUE, 10L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
    private Executor mBackgroundTaskExecutor = Executors.newSingleThreadExecutor();

    public static ThreadPoolManager getInstance() {
        return sThreadPoolManager;
    }

    public void executeForegroundTask(Task task) {
        mForegroundTaskExecutor.execute(task);
    }

    public void executeBackgroundTask(Task task) {
        mBackgroundTaskExecutor.execute(task);
    }
}
