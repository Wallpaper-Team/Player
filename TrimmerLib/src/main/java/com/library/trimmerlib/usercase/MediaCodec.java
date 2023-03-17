package com.library.trimmerlib.usercase;

import com.marvhong.videoeffect.FillMode;
import com.marvhong.videoeffect.composer.Mp4Composer;
import com.marvhong.videoeffect.helper.MagicFilterFactory;

public class MediaCodec {

    private Mp4Composer mMp4Composer;

    public MediaCodec(String srcPath, String outPath) {
        mMp4Composer = new Mp4Composer(srcPath, outPath);
    }

    public MediaCodec fillMode(FillMode mode) {
        mMp4Composer.fillMode(mode);
        return this;
    }

    //public MediaCodec filter()
}
