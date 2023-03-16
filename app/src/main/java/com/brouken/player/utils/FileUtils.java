package com.brouken.player.utils;

import static com.brouken.player.utils.Constants.CONTENTTYPE_FB2;
import static com.brouken.player.utils.Constants.CONTENTTYPE_OCTETSTREAM;
import static com.brouken.player.utils.Constants.CONTENTTYPE_OGG;
import static com.brouken.player.utils.Constants.CONTENTTYPE_OPUS;
import static com.brouken.player.utils.Constants.CONTENTTYPE_RAR;

import android.webkit.MimeTypeMap;

public class FileUtils {

    public static boolean isVideo(String name) {
        String mime = getTypeByName(name);
        return mime.startsWith("video/");
    }

    public static boolean isAudio(String name) {
        String mime = getTypeByName(name);
        return mime.startsWith("audio/");
    }

    public static String getTypeByName(String name) {
        String ext = getExt(name);
        return getTypeByExt(ext);
    }


    public static String getNameNoExt(String name) {
        int i = name.lastIndexOf('.');
        if (i > 0) name = name.substring(0, i);
        return name;
    }

    public static String getExt(String name) { // FilenameUtils.getExtension(n)
        int i = name.lastIndexOf('.');
        if (i > 0) return name.substring(i + 1);
        return "";
    }

    public static String getTypeByExt(String ext) {
        if (ext == null || ext.isEmpty()) return CONTENTTYPE_OCTETSTREAM; // replace 'null'
        ext = ext.toLowerCase();
        switch (ext) {
            case "opus":
                return CONTENTTYPE_OPUS; // android missing
            case "ogg":
                return CONTENTTYPE_OGG; // replace 'application/ogg'
            case "fb2":
                return CONTENTTYPE_FB2;
            case "rar":
                return CONTENTTYPE_RAR;
        }
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        if (type == null) return CONTENTTYPE_OCTETSTREAM;
        return type;
    }

}
