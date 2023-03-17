package com.module.core.repository;

import com.module.core.model.MediaData;

import java.util.List;

public interface MediaDataResource {

    List<MediaData> loadAll();
}
