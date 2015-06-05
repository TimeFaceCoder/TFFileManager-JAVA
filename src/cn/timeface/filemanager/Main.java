package cn.timeface.filemanager;

import cn.timeface.filemanager.uploadmanager.UploadInfo;
import cn.timeface.filemanager.uploadmanager.UploadManager;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            List<String> uploadFiles = new ArrayList<>(10);
            uploadFiles.add("/Users/rayboot/Downloads/1.jpg");
            uploadFiles.add("/Users/rayboot/Downloads/FXoFEf9k.jpg");
            uploadFiles.add("/Users/rayboot/Downloads/IMG_1349.JPG");
            UploadInfo uploadInfo = new UploadInfo("shiyanv", uploadFiles).setZip(true).setCheckMD5(true).addParams("zip", "1");
            UploadManager.getInstance().addTask(uploadInfo, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
