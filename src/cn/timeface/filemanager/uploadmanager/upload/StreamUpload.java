package cn.timeface.filemanager.uploadmanager.upload;

import cn.timeface.filemanager.uploadmanager.ManagerThreadRunnable;
import cn.timeface.filemanager.uploadmanager.UploadInfo;
import cn.timeface.filemanager.uploadmanager.UploadManager;
import cn.timeface.filemanager.utils.Constants;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import okio.BufferedSink;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by rayboot on 15/5/29.
 */
public class StreamUpload extends UploadStrategy {
    //总上传进度
    long totalUploadedLength = 0;
    long totalLength = 0;

    boolean cancel = false;

    public StreamUpload(UploadInfo uploadInfo) {
        super(uploadInfo);
    }

    final class PostDataRunnable extends ManagerThreadRunnable {
        int blockIndex = 0;
        long blockSize = 0;
        File uploadFile;
        byte[] buff = new byte[Constants.CHUCK_SIZE];

        public PostDataRunnable(File uploadFile, int blockIndex, long blockSize) {
            this.blockIndex = blockIndex;
            this.blockSize = blockSize;
            this.uploadFile = uploadFile;
        }

        public void runImpl() {
            //计算分片个数
            int chunkCount = (int) (blockSize / Constants.CHUCK_SIZE);
            chunkCount = blockSize % Constants.CHUCK_SIZE == 0 ? chunkCount : chunkCount + 1;

            FileInputStream inputStream = null;
            Response response;
            try {
                //读取上传记录
                long recorder = UploadManager.getInstance().getRecorderStrategy().readRecorder(uploadInfo.getToken(), uploadFile.getAbsolutePath().hashCode(), blockIndex, blockSize);
                totalUploadedLength += recorder;

                inputStream = new FileInputStream(uploadFile);
                //将来源文件的指针偏移至开始位置
                inputStream.skip(blockIndex * Constants.BLOCK_SIZE + recorder);

                int chunkIndex = recorder % Constants.CHUCK_SIZE == 0 ? (int) (recorder / Constants.CHUCK_SIZE) : (int) (recorder / Constants.CHUCK_SIZE) + 1;

                for (; chunkIndex < chunkCount; chunkIndex++) {
                    if (cancel) {
                        break;
                    }

                    //获取最终上传的大小
                    int uploadSize = (chunkIndex == chunkCount - 1 && blockSize % Constants.CHUCK_SIZE > 0) ? (int) (blockSize % Constants.CHUCK_SIZE) : Constants.CHUCK_SIZE;
                    //读取数据到buff中
                    int res = inputStream.read(buff, 0, uploadSize);
                    //获取requestbody
                    RequestBody requestBody = getInputStreamRequestBody(getMediaType(), buff, uploadSize);

                    response = postBlock(blockIndex, blockSize, chunkIndex, requestBody);
                    if (response.isSuccessful()) {
                        //添加md5值校验
                        //没做
                        //待补充

                        //整个任务的上传进度
                        totalUploadedLength += res;
                        //单个文件上传进度
                        recorder += res;

                        //存储块上传进度
                        UploadManager.getInstance().getRecorderStrategy().writeRecorder(uploadInfo.getToken(), uploadFile.getAbsolutePath().hashCode(), blockIndex, blockSize, recorder);
                        //回调整个任务上传进度
                        UploadManager.getInstance().getUploadStateListener().taskProgress(uploadInfo.getToken(), uploadInfo.getTitle(), totalUploadedLength, totalLength);
                        //回调单个文件上传进度
                        UploadManager.getInstance().getUploadStateListener().fileProgress(uploadInfo.getToken(), uploadFile.getAbsolutePath(), recorder, uploadFile.length());
                    } else {
                        //上传异常处理
                    }
                }
                if (totalUploadedLength >= totalLength) {
                    mkFile(uploadFile.length());

                    //删除缓存信息
                    UploadManager.getInstance().getRecorderStrategy().deleteRecorder(uploadInfo.getToken());
                    //回调任务完成事件
                    UploadManager.getInstance().getUploadStateListener().taskComplete(uploadInfo);
                }

                if (recorder >= uploadFile.length()) {
                    //回调任务完成事件
                    UploadManager.getInstance().getUploadStateListener().fileComplete(uploadInfo, uploadFile.getAbsolutePath());
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    //关闭数据流
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    @Override
    public void upload() throws Exception {
        File zipFile = null;
        totalLength = 0;
        if (uploadInfo.isGzip()) {
            zipFile = uploadInfo.getZipFile(UploadManager.getInstance().getRecorderStrategy().getRecorderDir());
            if (zipFile == null) {
                uploadInfo.setGzip(false);
            }
        }

        if (uploadInfo.isGzip() && (zipFile != null && zipFile.exists())) {
            totalLength = zipFile.length();
            upload(zipFile);
        } else if (uploadInfo.getFilePaths().size() > 0) {
            for (String filePath : uploadInfo.getFilePaths()) {
                File file = new File(filePath);
                totalLength += file.length();
                upload(file);
            }
        }
    }

    @Override
    public void cancel() throws Exception {
        cancel = true;
    }

    private void upload(File uploadFile) {
        cancel = false;

        long fileSize = uploadFile.length();
        //分块
        int blockCount = (int) (fileSize / Constants.BLOCK_SIZE);
        blockCount = fileSize % Constants.BLOCK_SIZE == 0 ? blockCount : blockCount + 1;

        for (int blockIndex = 0; blockIndex < blockCount; blockIndex++) {
            PostDataRunnable runnable = new PostDataRunnable(uploadFile,
                    blockIndex,
                    (blockIndex == blockCount - 1 && fileSize % Constants.BLOCK_SIZE > 0) ? fileSize % Constants.BLOCK_SIZE : Constants.BLOCK_SIZE);
            UploadManager.getInstance().getMultiThreadExecutorService().submit(runnable);
        }
    }

    @Override
    public MediaType getMediaType() {
        return MediaType.parse(uploadInfo.getMimeType());
    }

    private Response postBlock(int blockIndex, long blockSize, int chunkIndex, RequestBody requestBody) throws IOException {
        //上传数据，如果是第一块则调用mkBlock 否则 调用putBlock
        if (chunkIndex == 0) {
            return mkBlock(blockIndex, blockSize, requestBody);
        } else {
            return putBlock(blockIndex, chunkIndex, requestBody);
        }
    }

    private Response mkBlock(int blockIndex, long blockSize, RequestBody requestBody) throws IOException {
        String url = Constants.MK_BLOCK + "/" + blockSize + "-" + blockIndex; //第一个chunk 使用 mkblock接口
        return postData(url, requestBody);
    }

    private Response putBlock(int blockIndex, int chunkIndex, RequestBody requestBody) throws IOException {
        String url = Constants.PUT_BLOCK + "/" + (chunkIndex * Constants.CHUCK_SIZE) + "-" + blockIndex + "-" + chunkIndex; // 其他chunk使用putblock接口
        return postData(url, requestBody);
    }

    private Response postData(String postUrl, RequestBody requestBody) throws IOException {
        Request request = new Request.Builder()
                .addHeader("uploadToken", uploadInfo.getToken())
                .url(postUrl)
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        } else {
            System.out.println("post url = " + request.urlString());
            System.out.println("post out info = " + response.body().string());
        }
        return response;
    }

    private void mkFile(long fileSize) throws IOException {
        Request request = new Request.Builder()
                .addHeader("Content-Type", "text/plain")
                .addHeader("uploadToken", uploadInfo.getToken())
                .addHeader("Content-Length", fileSize + "")
                .url(Constants.MK_FILE + "/" + fileSize)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        } else {
            System.out.println("post url = " + request.urlString());
            System.out.println("post out info = " + response.body().string());
        }
    }

    public RequestBody getInputStreamRequestBody(final MediaType mediaType, final byte[] source, final int length) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return mediaType;
            }

            @Override
            public long contentLength() {
                return length;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.write(source, 0, length);
            }

        };
    }
}