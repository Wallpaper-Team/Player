package com.module.core.execution;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CoreExecution {

    private static CoreExecution sInstance;
    private Executor mIO;

    private CoreExecution() {
        mIO = Executors.newFixedThreadPool(1);
    }

    public static CoreExecution getInstance() {
        if (sInstance == null) {
            sInstance = new CoreExecution();
        }
        return sInstance;
    }

    public Executor getIO() {
        return mIO;
    }
}
