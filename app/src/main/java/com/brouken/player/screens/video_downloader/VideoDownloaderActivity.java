package com.brouken.player.screens.video_downloader;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.brouken.player.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class VideoDownloaderActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secure_folder);
    }
}