package com.module.core.usercase;

import com.module.core.model.MediaData;
import com.module.core.repository.MediaRepository;

import java.util.List;

public class LoadAll {

    private MediaRepository mRepository;

    public LoadAll(MediaRepository repository) {
        mRepository = repository;
    }

    public List<MediaData> invoke() {
        return mRepository.loadAll();
    }
}
