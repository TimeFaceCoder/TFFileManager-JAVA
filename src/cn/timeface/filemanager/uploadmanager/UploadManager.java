package cn.timeface.filemanager.uploadmanager;

import cn.timeface.filemanager.uploadmanager.recorder.RecorderStrategy;
import cn.timeface.filemanager.uploadmanager.recorder.SimpleFileRecorderStrategy;
import cn.timeface.filemanager.uploadmanager.upload.FormUpload;
import cn.timeface.filemanager.uploadmanager.upload.StreamUpload;
import cn.timeface.filemanager.uploadmanager.upload.UploadStrategy;
import cn.timeface.filemanager.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author rayboot
 * @from 15/5/28 11:25
 * @TODO
 */
public class UploadManager {

    private static UploadManager uploadManager;

    RecorderStrategy recorderStrategy;
    Map<String, UploadStrategy> taskMap = new HashMap<>(10);

    IUploadStateListener uploadStateListener;

    public static UploadManager getInstance() {
        if (uploadManager == null) {
            uploadManager = new UploadManager();
        }
        return uploadManager;
    }

    public UploadManager() {
    }

    public RecorderStrategy getRecorderStrategy() {
        if (recorderStrategy == null) {
            recorderStrategy = new SimpleFileRecorderStrategy("/Users/rayboot/Downloads/");
        }
        return recorderStrategy;
    }

    public void setRecorderStrategy(RecorderStrategy recorderStrategy) {
        this.recorderStrategy = recorderStrategy;
    }

    public IUploadStateListener getUploadStateListener() {
        return uploadStateListener;
    }

    public void setUploadStateListener(IUploadStateListener uploadStateListener) {
        this.uploadStateListener = uploadStateListener;
    }

    private ExecutorService mMultiThreadExecutor;
    static final float EXECUTOR_POOL_SIZE_PER_CORE = 1.5f;

    public ExecutorService getMultiThreadExecutorService() {
        if (null == mMultiThreadExecutor || mMultiThreadExecutor.isShutdown()) {
            final int numThreads = Math.round(Runtime.getRuntime().availableProcessors()
                    * EXECUTOR_POOL_SIZE_PER_CORE);
            mMultiThreadExecutor = Executors
                    .newFixedThreadPool(numThreads, new ManagerTreadFactory());

            System.out.println("app multi thread" + "MultiThreadExecutor created with "
                    + numThreads
                    + " threads");
        }
        return mMultiThreadExecutor;
    }

    public void cancelTask(String token) throws Exception {
        if (taskMap.get(token) != null) {
            taskMap.get(token).cancel();
        }

        if (uploadStateListener != null) {
            uploadStateListener.cancel(token);
        }
    }

    public void addTask(UploadInfo uploadInfo, UploadStrategy customStrategy) throws Exception {
        if (uploadStateListener != null) {
            uploadStateListener.start(uploadInfo);
        }

        if (uploadInfo.checkMD5) {
            try {
                uploadInfo.setFilePaths(new Check().doCheckFile(uploadInfo.getFilePaths()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (uploadInfo.getFilePaths().size() == 0) {
                //传完
                return;
            }
        }

        getRecorderStrategy().addRecorder(uploadInfo);

        //选择上传策略
        UploadStrategy uploadStrategy = new FormUpload(uploadInfo);
        switch (uploadInfo.getUploadStrategy()) {
            case UploadInfo.UPLOAD_STRATEGY_SMART:
                if (uploadInfo.getFilePaths().size() == 1 && new File(uploadInfo.getFilePaths().get(0)).length() < Constants.SINGLE_UPLOAD_MAX_SIZE) {
                    uploadStrategy = new FormUpload(uploadInfo);
                } else {
                    uploadStrategy = new StreamUpload(uploadInfo);
                }
                break;
            case UploadInfo.UPLOAD_STRATEGY_FORM:
                uploadStrategy = new FormUpload(uploadInfo);
                break;
            case UploadInfo.UPLOAD_STRATEGY_STREAM:
                uploadStrategy = new StreamUpload(uploadInfo);
                break;
            case UploadInfo.UPLOAD_STRATEGY_CUSTOM:
                uploadStrategy = customStrategy;
                break;
        }
        uploadStrategy.upload();
    }
}
