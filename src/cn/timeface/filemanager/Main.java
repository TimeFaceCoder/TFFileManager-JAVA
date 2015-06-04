package cn.timeface.filemanager;

import cn.timeface.filemanager.uploadmanager.UploadInfo;
import cn.timeface.filemanager.uploadmanager.UploadManager;
import cn.timeface.filemanager.uploadmanager.upload.UploadStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        UploadStrategy uploadStrategy;
//        File file = new File("/Users/rayboot/Downloads/1.jpg");
        File file1 = new File("/Users/rayboot/Downloads/FXoFEf9k.jpg");
//        File file2 = new File("/Users/rayboot/Downloads/IMG_1349.JPG");
        File bigFile = new File("/Users/rayboot/Desktop/时代空播/电影/生活大爆炸/16.mp4");
        try {
//            List<File> uploadFiles = new Check().doCheckFile(file2);
            List<String> uploadFiles = new ArrayList<>(10);
            uploadFiles.add(bigFile.getAbsolutePath());
            UploadInfo uploadInfo = new UploadInfo("shiyan", "shiyan", uploadFiles).setGzip(false);
            UploadManager.getInstance().addTask(uploadInfo, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
