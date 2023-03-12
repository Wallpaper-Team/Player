package com.brouken.player.screens.home;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.UriPermission;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.brouken.player.Prefs;
import com.brouken.player.R;
import com.brouken.player.domain.usecase.OpenFileUsecases;
import com.brouken.player.screens.home.model.MainOptionMenuItem;
import com.brouken.player.screens.player.PlayerActivity;
import com.brouken.player.databinding.ActivityHomeBinding;
import com.brouken.player.screens.home.view.MainOptionAdapter;
import com.brouken.player.screens.home.viewmodel.HomeViewModel;
import com.brouken.player.screens.settings.SettingsActivity;
import com.brouken.player.utils.Constants;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeActivity extends AppCompatActivity implements MainOptionAdapter.MainOptionItemOnClickListener {

    private ActivityHomeBinding mBinding;
    private HomeViewModel mHomeViewModel;
    @Inject
    MainOptionAdapter mAdapter;
    @Inject
    Prefs mPrefs;
    @Inject
    OpenFileUsecases mUsecases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mHomeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        mHomeViewModel.options.observe(this, options -> {
            mAdapter.setOptions(options);
        });
        mAdapter.setListener(this);
        mBinding.setAdapter(mAdapter);
        mBinding.fbt.setOnClickListener(v -> {
            startActivity(new Intent(this, PlayerActivity.class));
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if ((requestCode == Constants.REQUEST_CHOOSER_VIDEO || requestCode == Constants.REQUEST_CHOOSER_AUDIO) && resultCode == RESULT_OK) {
            boolean uriAlreadyTaken = false;
            final Uri uri = data.getData();
            // https://commonsware.com/blog/2020/06/13/count-your-saf-uri-permission-grants.html
            final ContentResolver contentResolver = getContentResolver();
            for (UriPermission persistedUri : contentResolver.getPersistedUriPermissions()) {
                if (persistedUri.getUri().equals(mPrefs.scopeUri)) {
                    continue;
                } else if (persistedUri.getUri().equals(uri)) {
                    uriAlreadyTaken = true;
                } else {
                    try {
                        contentResolver.releasePersistableUriPermission(persistedUri.getUri(), Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (/*!uriAlreadyTaken && */uri != null) {
                try {
                    contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    mUsecases.openFile(uri);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(MainOptionMenuItem option) {
        switch (option.getTitleId()) {
            case R.string.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.string.video_player:
                mUsecases.pickVideo();
                break;
            case R.string.music_player:
                mUsecases.pickAudio();
                break;
            default:
                break;
        }
    }
}