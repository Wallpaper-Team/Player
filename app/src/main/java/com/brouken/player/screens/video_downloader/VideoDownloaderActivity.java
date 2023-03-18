package com.brouken.player.screens.video_downloader;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import com.brouken.player.R;
import com.brouken.player.databinding.ActivityVideoDownloaderBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class VideoDownloaderActivity extends AppCompatActivity implements Player.Listener {

    private ActivityVideoDownloaderBinding mBinding;
    private DownloadManager mDownloadManager;
    private static boolean isDownloadCompleted;
    private long downloadImageId;
    private Thread trackingStatusThread;
    private BroadcastReceiver onComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (id == downloadImageId) {
                isDownloadCompleted = true;
                mBinding.btnDownload.setText(R.string.download);
                if (trackingStatusThread != null) {
                    trackingStatusThread.interrupt();
                    trackingStatusThread = null;
                }
                Toast.makeText(context, "Downloading completed!", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityVideoDownloaderBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mBinding.editQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    mBinding.txtError.setVisibility(View.GONE);
                } else if (Patterns.WEB_URL.matcher(mBinding.editQuery.getText().toString()).matches()) {
                    Toast.makeText(VideoDownloaderActivity.this, "Pattern Matches", Toast.LENGTH_SHORT).show();
                    mBinding.txtError.setVisibility(View.GONE);
                    initPlayer();
                } else {
                    // otherwise show error of invalid url
                    mBinding.txtError.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mBinding.editQuery.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                initPlayer();
                hideSoftInput();
            }
            return true;
        });
        mBinding.txtExample.setTypeface(null, Typeface.ITALIC);
        mBinding.txtExample.setOnClickListener(v -> {
            mBinding.editQuery.setText(getString(R.string.video_example_uri));
        });
        mBinding.btnDownload.setOnClickListener(v -> {
            if (mBinding.btnDownload.getText().equals(getString(R.string.download))) {
                startDownload(Uri.parse(mBinding.editQuery.getText().toString()));
            } else {
                mDownloadManager.remove(downloadImageId);
                mBinding.btnDownload.setText(R.string.download);
            }
        });
        mDownloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        requestStoragePermissionGranted();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                finish();
                Toast.makeText(this, "It is not available when permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private ExoPlayer player;
    private MediaItem mediaItem;

    private void initPlayer() {
        String uri = mBinding.editQuery.getText().toString();
        if (uri == null || uri.isEmpty()) {
            Toast.makeText(this, "Please enter valid url", Toast.LENGTH_SHORT).show();
            return;
        }
        mediaItem = MediaItem.fromUri(uri);
        player = new ExoPlayer.Builder(this).build();
        mBinding.videoPreview.setPlayer(player);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.addListener(this);
        mBinding.btnDownload.setEnabled(true);
    }

    private void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    public void onPlayerError(PlaybackException error) {
        Player.Listener.super.onPlayerError(error);
        mBinding.btnDownload.setEnabled(false);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Build.VERSION.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onComplete);
        if (trackingStatusThread != null) {
            trackingStatusThread.interrupt();
            trackingStatusThread = null;
        }
    }

    private void requestStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    private String getFileNameFromUri(Uri uri) {
        String path = uri.getPath();
        if (path.indexOf('/') == -1) {
            return path;
        }
        return path.substring(path.lastIndexOf('/'));
    }

    private void startDownload(Uri uri) {
        isDownloadCompleted = false;
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle("Download");
        String name = getFileNameFromUri(uri);
        request.setDescription(name);
        request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, name);
        downloadImageId = mDownloadManager.enqueue(request);
        mBinding.btnDownload.setText(android.R.string.cancel);
    }

    private void startDownloadStatusTracking(Long downloadImageId) {
        trackingStatusThread = new Thread() {
            @Override
            public void run() {
                while (!isDownloadCompleted) {
                    runOnUiThread(() -> {
                        String status = getStatusMessage(downloadImageId);
                        Toast.makeText(VideoDownloaderActivity.this, status, Toast.LENGTH_SHORT).show();
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        trackingStatusThread.start();
    }

    private String getStatusMessage(Long downloadId) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        if (mDownloadManager != null) {
            Cursor cursor = mDownloadManager.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                return getDownloadStatus(cursor);
            }
            return "NO_STATUS_INFO";
        }
        return "NO_STATUS_INFO";
    }

    private String getDownloadStatus(Cursor cursor) {
        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int status = cursor.getInt(columnIndex);
        int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
        int reason = cursor.getInt(columnReason);
        String statusText = "";
        String reasonText = "";
        switch (status) {
            case DownloadManager.STATUS_FAILED: {
                statusText = "STATUS_FAILED";
                switch (reason) {
                    case DownloadManager.ERROR_CANNOT_RESUME:
                        reasonText = "ERROR_CANNOT_RESUME";
                        break;
                    case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                        reasonText = "ERROR_DEVICE_NOT_FOUND";
                        break;
                    case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                        reasonText = "ERROR_FILE_ALREADY_EXISTS";
                        break;
                    case DownloadManager.ERROR_FILE_ERROR:
                        reasonText = "ERROR_FILE_ERROR";
                        break;
                    case DownloadManager.ERROR_HTTP_DATA_ERROR:
                        reasonText = "ERROR_HTTP_DATA_ERROR";
                        break;
                    case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                        reasonText = "ERROR_INSUFFICIENT_SPACE";
                        break;
                    case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                        reasonText = "ERROR_TOO_MANY_REDIRECTS";
                        break;
                    case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                        reasonText = "ERROR_UNHANDLED_HTTP_CODE";
                        break;
                    case DownloadManager.ERROR_UNKNOWN:
                        reasonText = "ERROR_UNKNOWN";
                        break;
                    default:
                        reasonText = "";
                        break;
                }
                break;
            }
            case DownloadManager.STATUS_PAUSED: {
                statusText = "STATUS_PAUSED";
                switch (reason) {
                    case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                        reasonText = "PAUSED_QUEUED_FOR_WIFI";
                        break;
                    case DownloadManager.PAUSED_UNKNOWN:
                        reasonText = "PAUSED_UNKNOWN";
                        break;
                    case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                        reasonText = "PAUSED_WAITING_FOR_NETWORK";
                        break;
                    case DownloadManager.PAUSED_WAITING_TO_RETRY:
                        reasonText = "PAUSED_WAITING_TO_RETRY";
                        break;
                    default:
                        reasonText = "";
                        break;
                }
                break;
            }
            case DownloadManager.STATUS_PENDING:
                statusText = "STATUS_PENDING";
                break;
            case DownloadManager.STATUS_RUNNING:
                statusText = "STATUS_RUNNING";
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                statusText = "STATUS_SUCCESSFUL";
                break;
        }
        return "Status: " + statusText + " " + reasonText;
    }
}