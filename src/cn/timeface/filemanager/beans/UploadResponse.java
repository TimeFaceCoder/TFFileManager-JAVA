package cn.timeface.filemanager.beans;

import java.util.List;

/**
 * Created by rayboot on 15/5/29.
 */
public class UploadResponse extends BaseResponse {
    List<String> fileList;

    public List<String> getFileList() {
        return fileList;
    }

    public void setFileList(List<String> fileList) {
        this.fileList = fileList;
    }
}
