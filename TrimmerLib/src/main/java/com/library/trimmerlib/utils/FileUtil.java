package com.library.trimmerlib.utils;

import android.net.Uri;
import android.util.Log;

import java.io.File;

public class FileUtil {

    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.delete()) {
                Log.i("dan.nv", "deleteFile: success");
                return;
            }
        }
        Log.i("dan.nv", "deleteFile: failed");
    }
}
