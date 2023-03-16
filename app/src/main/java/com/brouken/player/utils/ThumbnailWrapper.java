package com.brouken.player.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.CancellationSignal;
import android.provider.MediaStore;
import android.util.Size;

import androidx.annotation.RequiresApi;

import com.brouken.player.R;

import java.io.File;
import java.io.IOException;

public class ThumbnailWrapper {
    public static Bitmap createThumbnail(Context context, File file, int width, int height) throws IOException {
        if (file.isDirectory()) {
            return drawableToBitmap(context.getDrawable(R.drawable.padlock));
        }
        if (FileUtils.isAudio(file.getName())) {
            return createAudioThumbnail(context, file, width, height);
        } else if (FileUtils.isVideo(file.getName())) {
            return createVideoThumbnail(file, width, height);
        }
        return null;
    }

    public static Bitmap createVideoThumbnail(File file, int width, int height) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return ThumbnailUtils.createVideoThumbnail(file, new Size(width, height), null);
        } else {
            return ThumbnailUtils.createVideoThumbnail(file.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
        }
    }


    public static Bitmap createAudioThumbnail(Context context, File file, int width, int height) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return ThumbnailUtils.createAudioThumbnail(file, new Size(width, height), null);
        } else {
            return drawableToBitmap(context.getDrawable(R.drawable.musical));
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
