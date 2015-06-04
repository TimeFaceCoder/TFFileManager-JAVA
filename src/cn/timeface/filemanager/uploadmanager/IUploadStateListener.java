package cn.timeface.filemanager.uploadmanager;

/**
 * Created by rayboot on 15/6/3.
 */
public interface IUploadStateListener {

    //整个任务的进度
    void taskProgress(String token, String name, long totalUploadedLength, long totalLength);

    //任务重单个文件的进度
    void fileProgress(String token, String uploadingFilePath, long uploadedLength, long fileLength);

    //整个任务完成
    void taskComplete(UploadInfo uploadInfo);

    //任务中单个文件完成
    void fileComplete(UploadInfo uploadInfo, String uploadingFilePath);

    //开始任务
    void start(UploadInfo uploadInfo);

    //取消任务
    void cancel(String token);

    //上传异常
    void error(String token);
}
