package com.library.trimmerlib.executor;

import java.util.concurrent.Executor;

public interface MainExecutor {
    void execute(Runnable runnable);
}
