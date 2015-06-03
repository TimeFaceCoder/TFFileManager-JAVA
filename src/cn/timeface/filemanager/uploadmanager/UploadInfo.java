package cn.timeface.filemanager.uploadmanager;

import cn.timeface.filemanager.utils.ZipUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rayboot on 15/6/2.
 */
public class UploadInfo {
    public static final int UPLOAD_STRATEGY_SMART = 0;
    public static final int UPLOAD_STRATEGY_FORM = 1;
    public static final int UPLOAD_STRATEGY_STREAM = 2;
    public static final int UPLOAD_STRATEGY_CUSTOM = 3;


    List<String> filePaths;
    String token;
    boolean checkMD5 = false;
    boolean gzip = false;
    String mimeType = "application/octet-stream";
    int uploadStrategy = UPLOAD_STRATEGY_SMART;
    String title;

    Map<String, String> params = new HashMap<>(10);

    public UploadInfo(String title, String token, List<String> filePaths) {
        this.title = title;
        this.token = token;
        this.filePaths = filePaths;
    }

    public String getTitle() {
        return title;
    }

    public UploadInfo setTitle(String title) {
        this.title = title;
        return this;
    }

    public List<String> getFilePaths() {
        return filePaths;
    }

    public UploadInfo setFilePaths(List<String> filePaths) {
        this.filePaths = filePaths;
        return this;
    }

    public UploadInfo setToken(String token) {
        this.token = token;
        return this;
    }

    public UploadInfo setCheckMD5(boolean checkMD5) {
        this.checkMD5 = checkMD5;
        return this;
    }

    public UploadInfo setMimeType(String mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    public UploadInfo setParams(Map<String, String> params) {
        this.params = params;
        return this;
    }

    public UploadInfo addParams(String key, String value) {
        this.params.put(key, value);
        return this;
    }

    public int getUploadStrategy() {
        return uploadStrategy;
    }

    public UploadInfo setUploadStrategy(int uploadStrategy) {
        this.uploadStrategy = uploadStrategy;
        return this;
    }

    public String getToken() {
        return token;
    }

    public boolean isCheckMD5() {
        return checkMD5;
    }

    public String getMimeType() {
        return mimeType;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public boolean isGzip() {
        return gzip;
    }

    public UploadInfo setGzip(boolean gzip) {
        this.gzip = gzip;
        return this;
    }

    public File getZipFile(String tempDir) {
        if (!gzip) {
            throw new IllegalArgumentException("gzip is false");
        }

        File zipFile = new File(tempDir + "/" + getToken() + ".zip");
        if (zipFile.exists()) {
            return zipFile;
        }

        try {
            List<File> imageFiles = new ArrayList<>(10);
            for (String filePath : getFilePaths()) {
                imageFiles.add(new File(filePath));
            }
            FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
            ZipUtils.zipFiles(imageFiles, fileOutputStream);
            return zipFile;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
