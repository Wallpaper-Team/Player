package com.library.trimmerlib;

public class NativeLib {

    // Used to load the 'trimmerlib' library on application startup.
    static {
        System.loadLibrary("trimmerlib");
    }

    /**
     * A native method that is implemented by the 'trimmerlib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}