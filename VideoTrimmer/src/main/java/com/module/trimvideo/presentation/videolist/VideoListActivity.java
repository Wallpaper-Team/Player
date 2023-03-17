package com.module.trimvideo.presentation.videolist;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.module.core.model.MediaData;
import com.module.trimvideo.R;
import com.module.trimvideo.presentation.videolist.adapter.OnClickHandler;
import com.module.trimvideo.presentation.videolist.adapter.VideoListAdapter;
import com.module.trimvideo.presentation.videolist.viewmodel.VideoListViewModel;
import com.module.trimvideo.utils.CompressOption;
import com.module.trimvideo.utils.TrimVideoUtil;

public class VideoListActivity extends AppCompatActivity  implements OnClickHandler {

    private RecyclerView mVideoList;
    private ProgressBar mLoading;
    private VideoListAdapter mAdapter;
    private VideoListViewModel mViewModel;

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    mViewModel.loadVideos();
                } else {
                    finish();
                }
            });

    ActivityResultLauncher<Intent> videoTrimResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // TODO handle result
                if (result.getData() != null && result.getData().hasExtra(TrimVideoUtil.TRIMMED_VIDEO_PATH)) {
                    String path = result.getData().getStringExtra(TrimVideoUtil.TRIMMED_VIDEO_PATH);
                    Toast.makeText(getApplicationContext(), "Video saved with path: " + path, Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        initRecyclerView();
        mLoading = findViewById(R.id.video_list_loading);

        mViewModel = new ViewModelProvider(this).get(VideoListViewModel.class);
        initObserver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStorageVideo();
    }

    private void updateView(boolean loading) {
        if (loading) {
            mVideoList.setVisibility(View.GONE);
            mLoading.setVisibility(View.VISIBLE);
        } else {
            mVideoList.setVisibility(View.VISIBLE);
            mLoading.setVisibility(View.GONE);
        }
    }

    private void initRecyclerView() {
        mVideoList = findViewById(R.id.video_list);
        mVideoList.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new VideoListAdapter(this);
        mVideoList.setAdapter(mAdapter);
    }

    private void initObserver() {
        mViewModel.getVideos().observe(this, mediaData -> {
            mAdapter.setData(mediaData);
            updateView(false);
        });
    }

    @Override
    public void handleClick(MediaData data) {
        // start trim video activity
        Log.i("dan.nv", "handleClick: "+data.path);
        TrimVideoUtil.activity(data.path)
                .setCompressOption(new CompressOption()) //pass empty constructor for default compress option
                .start(this, videoTrimResultLauncher);
    }

    private void loadStorageVideo() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            mViewModel.loadVideos();
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(
                    Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }
}