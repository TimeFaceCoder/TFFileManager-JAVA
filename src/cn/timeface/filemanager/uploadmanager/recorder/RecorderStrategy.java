package cn.timeface.filemanager.uploadmanager.recorder;

import cn.timeface.filemanager.uploadmanager.UploadInfo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Created by rayboot on 15/6/2.
 */
public abstract class RecorderStrategy {
    //临时文件目录，主要用于存储压缩文件，或者文件记录之类
    String recorderDir;

    public RecorderStrategy(String recorderDir) {
        this.recorderDir = recorderDir;
    }

    public String getRecorderDir() {
        return recorderDir;
    }

    public void setRecorderDir(String recorderDir) {
        this.recorderDir = recorderDir;
    }

    //记录已上传大小
    public abstract void writeRecorder(String token, int filePathHash, int blockIndex, long blockSize, long uploadedSize) throws IOException;

    //读取已上传大小
    public abstract long readRecorder(String token, int filePathHash, int blockIndex, long blockSize) throws IOException;

    //删除所有记录
    public abstract void deleteRecorder(String token);

    //添加任务
    public abstract void addRecorder(UploadInfo uploadInfo) throws IOException;

    //获取上传任务列表
    public abstract List<UploadInfo> getAllRecorders() throws IOException;
}
