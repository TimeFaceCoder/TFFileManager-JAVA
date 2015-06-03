package cn.timeface.filemanager.uploadmanager.upload;

import cn.timeface.filemanager.uploadmanager.UploadInfo;
import cn.timeface.filemanager.utils.Constants;
import com.squareup.okhttp.*;

import java.io.File;
import java.io.IOException;

/**
 * Created by rayboot on 15/5/29.
 */
public class FormUpload extends UploadStrategy {

    public FormUpload(UploadInfo uploadInfo) {
        super(uploadInfo);
    }

    @Override
    public void upload() throws Exception {
        for (String filePath : uploadInfo.getFilePaths()) {
            File file = new File(filePath);
            run(file);
        }

    }

    @Override
    public void cancel() throws Exception {

    }

    public void run(File file) throws Exception {
        RequestBody requestBody =
                new MultipartBuilder().type(MultipartBuilder.FORM)
                        .addFormDataPart("uploadToken", uploadInfo.getToken())
                        .addFormDataPart("fileName", file.getName())
                        .addFormDataPart("file", file.getName(), RequestBody.create(getMediaType(), file))
                        .build();

        Request request = new Request.Builder()
                .addHeader("Content-Length", file.length() + "")
                .url(Constants.SINGLE_UPLOAD)
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {

            throw new IOException("Unexpected code " + response);
        }

        System.out.println(response.body().string());
    }

    public MediaType getMediaType() {
        return MediaType.parse(uploadInfo.getMimeType());
    }
}
