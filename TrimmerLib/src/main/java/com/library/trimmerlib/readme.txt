build jar: https://stackoverflow.com/questions/21712714/how-to-make-a-jar-out-from-an-android-studio-project


  public static String getTrimmedVideoPath(Context context, String dirName, String fileNamePrefix) {
    String finalPath = "";
    String dirPath = "";
    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
      dirPath = context.getExternalCacheDir() + File.separator + dirName; // /mnt/sdcard/Android/data/<package name>/files/...
    } else {
      dirPath = context.getCacheDir() + File.separator + dirName; // /data/data/<package name>/files/...
    }
    File file = new File(dirPath);
    if (!file.exists()) {
      file.mkdirs();
    }
    finalPath = file.getAbsolutePath();
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(new Date());
    String outputName = fileNamePrefix + timeStamp + ".mp4";
    finalPath = finalPath + "/" + outputName;
    return finalPath;
  }

  public static String getTrimmedPath(Context context, String dirName, String fileNamePrefix) {
    if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
      return getTrimmedVideoPath(context, dirName, fileNamePrefix);
    } else {
      String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
              .format(new Date());
      String outputName = fileNamePrefix + timeStamp + ".mp4";
      File file = new File(context.getExternalFilesDir(dirName), outputName);

      Log.i(TAG, "getTrimmedPath: "+file.getAbsolutePath());
      return file.getAbsolutePath();
    }
  }

  private static boolean isExternalStorageReadOnly() {
    String extStorageState = Environment.getExternalStorageState();
    if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
      return true;
    }
    return false;
  }

  private static boolean isExternalStorageAvailable() {
    String extStorageState = Environment.getExternalStorageState();
    if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
      return true;
    }
    return false;
  }


   new TrimVideo(ExecutorManager.getInstance(new MainThreadImpl()), new TrimVideo.OnTrimVideoListener() {
          @Override
          public void onSuccess(String outPath) {

          }

          @Override
          public void onComplete(String outPath) {
            Log.i(TAG, "onComplete: "+outPath);
            initVideoByURI(Uri.parse(outPath));
            invalidate();
          }

          @Override
          public void onProgress(int progress) {
            Log.i(TAG, "onProgress: "+progress);
          }

          @Override
          public void onCanceled() {

          }

          @Override
          public void onFailed(Exception e) {

          }
        }).invoke(mSourceUri.getPath(), StorageUtil.getTrimmedVideoPath(getContext(), "small_video/trimmedVideo",
                        "trimmedVideo_"), StorageUtil.getTrimmedPath(getContext(), "VietDan",
                        "filterVideo_"), mLeftProgressPos /1000,
                mRightProgressPos/1000);
