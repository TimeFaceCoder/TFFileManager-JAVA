时光流影上传组件**（JAVA    &  Android）**
===================


- 上传组件可以配置使用表单上传或者流上传。默认为<kbd>UPLOAD_STRATEGY_SMART</kbd>策略
- 流上传可以设置分块上传策略
- 可以设置是否检测文件MD5值，达到秒传功能
- 可以设置多文件是否zip压缩上传功能
- 对于多平台，可以设置不同的存储策略，用于存储临时文件，上传进度等。默认为文件存储，当然不同平台可以选用sql或者xml存储方式


----------


使用方法
-------------

####**UploadManager**
单例模式类，配置上传策略，存储策略，事件监听，线程池以及添加/取消上传任务的功能

####**UploadInfo**
上传任务类，设计为Builder模式，便于设置参数。
每一个上传任务，均为一个UploadInfo实例。

```java
    List<String> filePaths;
    String token;
    boolean checkMD5 = false;
    boolean zip = false;
    String mimeType = "application/octet-stream";
    int uploadStrategy = UPLOAD_STRATEGY_SMART;
    String title;
    Map<String, String> params = new HashMap<>(10);
```

> - **filePaths** 上传文件的路径列表
> - **token** 任务的唯一标识
> - **checkMD5** 是否对MD5进行校验
> - **zip** 是否对文件zip压缩上传
> - **mimeType** 文件类型
> - **uploadStrategy** 上传策略 

uploadStrategy 上传策略
> - **UPLOAD_STRATEGY_SMART** 单文件且文件大小小于<kbd>Constants.SINGLE_UPLOAD_MAX_SIZE</kbd>则使用表单上传，其他使用流上传
> - **UPLOAD_STRATEGY_FORM** 强制使用表单上传
> - **UPLOAD_STRATEGY_STREAM** 强制使用流上传
> - **UPLOAD_STRATEGY_CUSTOM** 自定义上传策略


####**UploadStrategy**
上传策略类
默认已经实现`FormUpload`和`StreamUpload`，可以自定义上传策略，只需实现对应方法

```java
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
```

####**RecorderStrategy**
存储策略类
默认已实现`SimpleFileRecorderStrategy` 存储策略必须在`UploadManager` 中初始化。
存储策略用于存储**zip生成的临时文件**和**上传进度**
```java
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

    //是否存在该任务
    public abstract boolean isRecorder(String token) throws IOException;
}

```


####**IUploadStateListener**
上传任务的回调事件
```java
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

```



上传流程
-------------
### **check**
检测文件在服务器上是否存在

### **uploadfile**
表单上传

### **mkblock**
流文件分块上传，上传块内第一片数据

### **putblock**
流文件分块上传，上传块内其他片数据

### **mkfile**
流文件上传最后文件合成

