package com.module.core.repository;

import com.module.core.model.MediaData;

import java.util.List;

public class MediaRepository {

    private MediaDataResource mResource;

    public MediaRepository(MediaDataResource resource) {
        mResource = resource;
    }

    public List<MediaData> loadAll() {
        return mResource.loadAll();
    }
}
