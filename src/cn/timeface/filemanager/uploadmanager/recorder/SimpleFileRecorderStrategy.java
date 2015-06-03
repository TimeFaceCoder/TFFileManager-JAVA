package cn.timeface.filemanager.uploadmanager.recorder;

import cn.timeface.filemanager.uploadmanager.UploadInfo;
import com.google.gson.Gson;
import com.squareup.okhttp.internal.Util;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rayboot on 15/6/2.
 */
public class SimpleFileRecorderStrategy extends RecorderStrategy {

    final String TASKS_DIR_NAME = "tasks";

    public SimpleFileRecorderStrategy(String recorderDir) {
        super(recorderDir);
        File dir = new File(recorderDir);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdir();
        }
    }

    //文件名格式为 filePathHash + "-" + blockIndex + "-" + blockSize
    //文件内容为  已上传的大小 uploadedSize
    @Override
    public void writeRecorder(String token, int filePathHash, int blockIndex, long blockSize, long uploadedSize) throws IOException {
        File file = new File(getRecorderDir() + "/" + token, filePathHash + "-" + blockIndex + "-" + blockSize);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }

        BufferedSink sink = Okio.buffer(Okio.sink(file));
        sink.writeLong(uploadedSize);
        Util.closeQuietly(sink);
    }

    //读取已上传大小uploadedSize
    @Override
    public long readRecorder(String token, int filePathHash, int blockIndex, long blockSize) throws IOException {
        File file = new File(getRecorderDir() + "/" + token, filePathHash + "-" + blockIndex + "-" + blockSize);
        if (!file.exists()) {
            return 0;
        }
        BufferedSource source = Okio.buffer(Okio.source(file));
        long uploadedSize = source.readLong();
        Util.closeQuietly(source);
        return uploadedSize;
    }

    //删除指定token的文件夹
    @Override
    public void deleteRecorder(String token) {
        delFolder(getRecorderDir() + "/" + token + "/");
        File taskFile = new File(getRecorderDir() + "/" + TASKS_DIR_NAME, token);
        if (taskFile.exists()) {
            taskFile.delete();
        }
    }

    @Override
    public void addRecorder(UploadInfo uploadInfo) throws IOException {
        File file = new File(getRecorderDir() + "/" + TASKS_DIR_NAME, uploadInfo.getToken());
        if (file.exists()) {
            return;
        }

        file.getParentFile().mkdirs();
        file.createNewFile();
        BufferedSink sink = Okio.buffer(Okio.sink(file));
        sink.writeUtf8(new Gson().toJson(uploadInfo));
        Util.closeQuietly(sink);
    }

    @Override
    public List<UploadInfo> getAllRecorders() throws IOException {
        File tasksDir = new File(getRecorderDir() + "/" + TASKS_DIR_NAME);
        List<UploadInfo> result = new ArrayList<>(10);
        if (!tasksDir.exists()) {
            return result;
        }

        for (File file : tasksDir.listFiles()) {
            BufferedSource source = Okio.buffer(Okio.source(file));
            String content = source.readUtf8();
            Util.closeQuietly(source);
            UploadInfo uploadInfo = new Gson().fromJson(content, UploadInfo.class);
            if (uploadInfo.getToken() != null && uploadInfo.getToken() != "") {
                result.add(uploadInfo);
            }
        }
        return result;
    }

    //删除文件夹
    //param folderPath 文件夹完整绝对路径
    public void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            File myFilePath = new File(folderPath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //删除指定文件夹下所有文件
    //param path 文件夹完整绝对路径
    public boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }


}
