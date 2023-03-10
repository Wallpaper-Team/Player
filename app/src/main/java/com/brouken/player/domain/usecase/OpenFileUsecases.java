package com.brouken.player.domain.usecase;

import static com.brouken.player.utils.Constants.REQUEST_CHOOSER_VIDEO;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.widget.Toast;

import com.brouken.player.Prefs;
import com.brouken.player.R;
import com.brouken.player.utils.Utils;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ActivityContext;

@Singleton
public class OpenFileUsecases {

    private Context mContext;
    @Inject
    Prefs mPrefs;
    private boolean restoreOrientationLock;

    @Inject
    public OpenFileUsecases(@ActivityContext Context context) {
        this.mContext = context;
    }

    public void enableRotation() {
        try {
            if (Settings.System.getInt(mContext.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION) == 0) {
                Settings.System.putInt(mContext.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 1);
                restoreOrientationLock = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openFile(Uri pickerInitialUri) {
        enableRotation();
        if (pickerInitialUri == null || Utils.isSupportedNetworkUri(pickerInitialUri)) {
            pickerInitialUri = Utils.getMoviesFolderUri();
        }

        final Intent intent = createBaseFileIntent(Intent.ACTION_OPEN_DOCUMENT, pickerInitialUri);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("video/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, Utils.supportedMimeTypesVideo);

        if (Build.VERSION.SDK_INT < 30) {
            final ComponentName systemComponentName = Utils.getSystemComponent(mContext, intent);
            if (systemComponentName != null) {
                intent.setComponent(systemComponentName);
            }
        }
        safelyStartActivityForResult(intent, REQUEST_CHOOSER_VIDEO);
    }

    private void safelyStartActivityForResult(final Intent intent, final int code) {
        if (intent.resolveActivity(mContext.getPackageManager()) == null)
            Toast.makeText(mContext, mContext.getText(R.string.error_files_missing), Toast.LENGTH_SHORT).show();
        else ((Activity) mContext).startActivityForResult(intent, code);
    }

    private Intent createBaseFileIntent(final String action, final Uri initialUri) {
        final Intent intent = new Intent(action);
        // http://stackoverflow.com/a/31334967/1615876
        intent.putExtra("android.content.extra.SHOW_ADVANCED", true);
        if (Build.VERSION.SDK_INT >= 26 && initialUri != null) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, initialUri);
        }
        return intent;
    }
}
