package com.module.trimvideo.framework.thread;

import android.os.Handler;
import android.os.Looper;

import com.library.trimmerlib.executor.MainExecutor;

public class MainThreadImpl implements MainExecutor {
    private Handler mHandler;

    public MainThreadImpl() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void execute(Runnable runnable) {
        mHandler.post(runnable);
    }
}
