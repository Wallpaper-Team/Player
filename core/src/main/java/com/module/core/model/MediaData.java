package com.module.core.model;

public class MediaData {

    public String path;
    public long size;
    public int height;
    public int width;
    public String album;
    public String name;
    public long duration;

    public MediaData(String path, long size, int height, int width, String album, String name, long duration) {
        this.path = path;
        this.size = size;
        this.height = height;
        this.width = width;
        this.album = album;
        this.name = name;
        this.duration = duration;
    }
}
