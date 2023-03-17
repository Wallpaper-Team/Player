package com.module.trimvideo.framework;


import android.content.Context;

import com.module.core.model.MediaData;
import com.module.core.repository.MediaDataResource;
import com.module.trimvideo.framework.provider.VideoProvider;

import java.util.ArrayList;
import java.util.List;

public class VideoDataResource implements MediaDataResource {

    private Context mContext;
    private static List<MediaData> sVideos = new ArrayList<>();

    public VideoDataResource(Context context) {
        mContext = context;
    }

    @Override
    public List<MediaData> loadAll() {
        if (sVideos.size() == 0) {
            sVideos = VideoProvider.getAllVideos(mContext);
        }
        return sVideos;
    }
}
