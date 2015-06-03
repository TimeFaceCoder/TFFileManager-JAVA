package cn.timeface.filemanager.uploadmanager;

import cn.timeface.filemanager.beans.CheckResponse;
import cn.timeface.filemanager.utils.Constants;
import cn.timeface.filemanager.utils.MD5Util;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rayboot
 * @from 15/5/22 10:39
 * @TODO
 */
public class Check {
    private final OkHttpClient client = new OkHttpClient();

    //返回需要上传的file，或者抛出异常
    public List<String> doCheckFile(List<String> filePaths) throws IOException {
        StringBuilder sizeList = new StringBuilder(10);
        StringBuilder md5List = new StringBuilder(10);
        Map<String, File> md5FileMap = new HashMap<>(10);

        //生成md5 file的map
        //生成check params
        for (String filePath : filePaths) {
            File file = new File(filePath);
            String md5 = getFileMD5(file);
//            if (md5FileMap.get(md5) != null) {
//                continue;
//            }
            md5FileMap.put(md5, file);
            md5List.append(md5);
            md5List.append(",");
            sizeList.append(file.length());
            sizeList.append(",");
        }

        if (sizeList.length() > 0) {
            sizeList.deleteCharAt(sizeList.length() - 1);
        }
        if (md5List.length() > 0) {
            md5List.deleteCharAt(md5List.length() - 1);
        }

        StringBuilder params = new StringBuilder(10);
        params.append("sizeList=");
        params.append(sizeList);
        params.append("&");
        params.append("md5List=");
        params.append(md5List);

        //请求检测接口
        Request request = new Request.Builder()
                .addHeader("Content-Type", "text/plain; charset=utf-8")
                .url(Constants.CHECK + "?" + params.toString())
                .build();

        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            String result = response.body().string();
            System.out.println("check out  " + result);
            CheckResponse checkResponse = new Gson().fromJson(result, CheckResponse.class);
            List<String> resultList = new ArrayList<>(10);
            for (String md5 : checkResponse.getChecksum()) {
                resultList.add(md5FileMap.get(md5).getAbsolutePath());
            }
            return resultList;
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    public String getFileMD5(File file) throws IOException {
        return MD5Util.getFileMD5String(file);
    }
}
