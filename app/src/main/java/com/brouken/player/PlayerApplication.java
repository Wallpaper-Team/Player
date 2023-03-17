package com.brouken.player;

import android.content.IntentFilter;
import android.util.Log;

import com.brouken.player.receiver.PlayReceiver;
import com.example.file_explorer.app.FilesApplication;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class PlayerApplication extends FilesApplication {

    private PlayReceiver mPlayReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayReceiver = new PlayReceiver();
        registerReceiver(mPlayReceiver, new IntentFilter("com.ducky.videoplayer.dk.VIEW"));
    }

    @Override
    public void onTerminate() {
        unregisterReceiver(mPlayReceiver);
        super.onTerminate();
    }
}
