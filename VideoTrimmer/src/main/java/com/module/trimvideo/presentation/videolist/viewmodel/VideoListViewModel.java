package com.module.trimvideo.presentation.videolist.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.module.core.execution.CoreExecution;
import com.module.core.model.MediaData;
import com.module.core.repository.MediaRepository;
import com.module.core.usercase.LoadAll;
import com.module.trimvideo.framework.VideoDataResource;

import java.util.List;

public class VideoListViewModel extends AndroidViewModel {

    private Context mContext;
    private CoreExecution mExecution;
    private MutableLiveData<List<MediaData>> mVideos = new MutableLiveData<>();
    private MediaRepository mMediaRepository;

    public VideoListViewModel(@NonNull Application application) {
        super(application);
        mContext = application;
        mMediaRepository = new MediaRepository(new VideoDataResource(application));
        mExecution = CoreExecution.getInstance();
    }

    public void loadVideos() {
        mExecution.getIO().execute(() -> {
            List<MediaData> videos = new LoadAll(mMediaRepository).invoke();
            mVideos.postValue(videos);
        });
    }

    public LiveData<List<MediaData>> getVideos() {
        return mVideos;
    }
}
