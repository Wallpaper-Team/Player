package com.module.trimvideo.utils;

import android.util.Log;

public class LogMessage {

   public static final boolean IS_LOG = true;

    public static void v(String msg) {
        if (IS_LOG)
            Log.v("VIDEO_TRIMMER ::", msg);
    }

    public static void e(String msg) {
        if (IS_LOG)
            Log.e("VIDEO_TRIMMER ::", msg);
    }

    public static void i(String msg) {
        if (IS_LOG) Log.i("VIDEO_TRIMMER ::",msg);
    }

    public static void d(String msg) {
        if (IS_LOG) Log.d("VIDEO_TRIMMER ::",msg);
    }
}
