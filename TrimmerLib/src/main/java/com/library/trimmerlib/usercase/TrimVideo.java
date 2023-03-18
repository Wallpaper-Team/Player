package com.library.trimmerlib.usercase;

import android.util.Log;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;
import com.library.trimmerlib.executor.ExecutorManager;
import com.library.trimmerlib.utils.FileUtil;
import com.marvhong.videoeffect.FillMode;
import com.marvhong.videoeffect.composer.Mp4Composer;
import com.marvhong.videoeffect.helper.MagicFilterFactory;

import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrimVideo {

    private static final String TAG = "dan.nv";
    private ExecutorManager mExecutorManager;
    private OnTrimVideoListener mCallback;

    private boolean mCanceled = false;

    private Mp4Composer mComposer;

    public TrimVideo(ExecutorManager executorManager, OnTrimVideoListener callback) {
        mCallback = callback;
        mExecutorManager = executorManager;
    }

    public void onDestroy() {
        mCallback = null;
    }

    public void invoke(final String src, final String dest_trimmer, final String dest_filter, final double startSec, final double endSec) {
        mExecutorManager.getWorkExecutor().execute(() -> {

            try {
                if (mCanceled) {
                    if (mCallback != null) mCallback.onCanceled();
                }
                double startSecond = startSec;
                double endSecond = endSec;
                Movie movie = MovieCreator.build(src);
                List<Track> tracks = movie.getTracks();
                movie.setTracks(new ArrayList<Track>());

                boolean timeCorrected = false;
                // Here we try to find a track that has sync samples. Since we can only start decoding
                // at such a sample we SHOULD make sure that the start of the new fragment is exactly
                // such a frame
                for (Track track : tracks) {
                    if (track.getSyncSamples() != null && track.getSyncSamples().length > 0) {
                        if (timeCorrected) {
                            // This exception here could be a false positive in case we have multiple tracks
                            // with sync samples at exactly the same positions. E.g. a single movie containing
                            // multiple qualities of the same video (Microsoft Smooth Streaming file)

                            throw new RuntimeException(
                                    "The startTime has already been corrected by another track with SyncSample. Not Supported.");
                        }
                        //矫正开始时间
                        startSecond = correctTimeToSyncSample(track, startSecond, false);
                        //矫正结束时间
                        endSecond = correctTimeToSyncSample(track, endSecond, true);

                        timeCorrected = true;
                    }
                }

                //裁剪后的位置   startSecond:299400, endSecond:309390
                //矫正后的位置   startSecond:291.3327083333511, endSecond:313.18787500003214
                //Log.e(TAG, "startSecond:" + startSecond + ", endSecond:" + endSecond);

                //fix bug: 部分视频矫正过后会超出10s,这里进行强制限制在10s内
                if (endSecond - startSecond > 10) {
                    int duration = (int) (endSec - startSec);
                    endSecond = startSecond + duration;
                }
                //fix bug: 部分视频裁剪后endSecond=0.0,导致播放失败
                if (endSecond == 0.0) {
                    int duration = (int) (endSec - startSec);
                    endSecond = startSecond + duration;
                }

                for (Track track : tracks) {
                    long currentSample = 0;
                    double currentTime = 0;
                    double lastTime = -1;
                    long startSample = -1;
                    long endSample = -1;

                    for (int i = 0; i < track.getSampleDurations().length; i++) {
                        long delta = track.getSampleDurations()[i];

                        if (currentTime > lastTime && currentTime <= startSecond) {
                            // current sample is still before the new starttime
                            startSample = currentSample;
                        }
                        if (currentTime > lastTime && currentTime <= endSecond) {
                            // current sample is after the new start time and still before the new endtime
                            endSample = currentSample;
                        }

                        lastTime = currentTime;
                        //计算出某一帧的时长 = 采样时长 / 时间长度
                        currentTime +=
                                (double) delta / (double) track.getTrackMetaData().getTimescale();
                        //这里就是帧数（采样）加一
                        currentSample++;
                    }
                    //在这里，裁剪是根据关键帧进行裁剪的，而不是指定的开始时间和结束时间
                    //startSample:2453, endSample:2846   393
                    //startSample:4795, endSample:5564   769
                    //Log.e(TAG, "startSample:" + startSample + ", endSample:" + endSample);
                    movie.addTrack(new CroppedTrack(track, startSample, endSample));

                    Container out = new DefaultMp4Builder().build(movie);
                    FileOutputStream fos = new FileOutputStream(String.format(dest_trimmer));
                    FileChannel fc = fos.getChannel();
                    out.writeContainer(fc);

                    fc.close();
                    fos.close();
                }

                mExecutorManager.getMainExecutor().execute(() -> {
                    if (mCallback != null) mCallback.onSuccess(dest_trimmer);
                });

                //startMediaCodec(dest_trimmer, dest_filter);

            } catch (Exception e) {
                mExecutorManager.getMainExecutor().execute(() -> {
                    if (mCallback != null) mCallback.onFailed(e);
                });
            }
            // todo check late: complete trimmer
            //mExecutorManager.getMainExecutor().execute(() -> mCallback.onComplete());
        });
    }

    public void cancel() {
        mCanceled = true;
        if (mComposer != null) mComposer.cancel();
    }

    private static double correctTimeToSyncSample(Track track, double cutHere, boolean next) {
        double[] timeOfSyncSamples = new double[track.getSyncSamples().length];
        long currentSample = 0;
        double currentTime = 0;
        for (int i = 0; i < track.getSampleDurations().length; i++) {
            long delta = track.getSampleDurations()[i];

            if (Arrays.binarySearch(track.getSyncSamples(), currentSample + 1) >= 0) {
                // samples always start with 1 but we start with zero therefore +1（采样的下标从1开始而不是0开始，所以要+1 ）
                timeOfSyncSamples[Arrays
                        .binarySearch(track.getSyncSamples(), currentSample + 1)] = currentTime;
            }
            currentTime += (double) delta / (double) track.getTrackMetaData().getTimescale();
            currentSample++;

        }
        double previous = 0;
        for (double timeOfSyncSample : timeOfSyncSamples) {
            if (timeOfSyncSample > cutHere) {
                if (next) {
                    return timeOfSyncSample;
                } else {
                    return previous;
                }
            }
            previous = timeOfSyncSample;
        }
        return timeOfSyncSamples[timeOfSyncSamples.length - 1];
    }

    private void startMediaCodec(String srcPath, String outputPath) {
        if (mCanceled) {
            if (mCallback != null) mCallback.onCanceled();
        }
        mComposer = new Mp4Composer(srcPath, outputPath)
                // .rotation(Rotation.ROTATION_270)
                //.size(720, 1280)
                .fillMode(FillMode.PRESERVE_ASPECT_FIT)
                .filter(MagicFilterFactory.getFilter())
                .mute(false)
                .flipHorizontal(false)
                .flipVertical(false)
                .listener(new Mp4Composer.Listener() {
                    @Override
                    public void onProgress(double progress) {
                        mExecutorManager.getMainExecutor().execute(() -> {
                            if (mCallback != null) mCallback.onProgress((int) (progress * 100));
                        });
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "filterVideo---onCompleted");
                        FileUtil.deleteFile(srcPath);
                        mExecutorManager.getMainExecutor().execute(() -> {
                            if (mCallback != null) {
                                mCallback.onComplete(outputPath);
                            }
                        });
/*                        runOnUiThread(() -> {
                            compressVideo(outputPath);
                        });*/
                    }

                    @Override
                    public void onCanceled() {
                        mExecutorManager.getMainExecutor().execute(() -> {
                            if (mCallback != null) {
                                mCallback.onCanceled();
                            }
                        });
                        //NormalProgressDialog.stopLoading();
                    }

                    @Override
                    public void onFailed(Exception exception) {
                        Log.e(TAG, "filterVideo---onFailed()");
                        mExecutorManager.getMainExecutor().execute(() -> {
                            if (mCallback != null) {
                                mCallback.onFailed(exception);
                            }
                        });
                        /*NormalProgressDialog.stopLoading();
                        Toast.makeText(TrimVideoActivity.this, "视频处理失败", Toast.LENGTH_SHORT).show();*/
                    }
                })
                .start();
    }

    public interface OnTrimVideoListener {
        void onSuccess(String outPath);

        void onComplete(String outPath);

        void onProgress(int progress);

        void onCanceled();

        void onFailed(Exception e);
    }
}
