package cn.timeface.filemanager.uploadmanager.upload;

import cn.timeface.filemanager.uploadmanager.UploadInfo;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;

/**
 * @author rayboot
 * @from 15/5/22 14:02
 * @TODO
 */
public abstract class UploadStrategy {
    public final int RETRY_COUNT = 3;
    protected final OkHttpClient client = new OkHttpClient();
    protected UploadInfo uploadInfo;

    public UploadStrategy(UploadInfo uploadInfo) {
        this.uploadInfo = uploadInfo;
    }

    //开始下载
    public abstract void upload() throws Exception;

    //取消下载
    public abstract void cancel() throws Exception;

    public abstract MediaType getMediaType();
}
