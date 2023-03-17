package com.library.trimmerlib.executor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ExecutorManager {

    private static ExecutorManager sInstance;
    private MainExecutor mMainExecutor;
    private Executor mIOExecutor;
    private Executor mWork;

    private ExecutorManager(MainExecutor mainExecutor) {
        mMainExecutor = mainExecutor;
        mIOExecutor = Executors.newSingleThreadExecutor();
        mWork = Executors.newFixedThreadPool(3);
    }

    public static ExecutorManager getInstance(MainExecutor mainExecutor) {
        if (sInstance == null) {
            sInstance = new ExecutorManager(mainExecutor);
        }
        return sInstance;
    }

    public MainExecutor getMainExecutor() {
        return mMainExecutor;
    }

    public Executor getIOExecutor() {
        return mIOExecutor;
    }

    public Executor getWorkExecutor() {
        return mWork;
    }
}
