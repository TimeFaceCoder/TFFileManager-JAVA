package cn.timeface.filemanager.utils;

import sun.misc.BASE64Decoder;

import java.io.IOException;

/**
 * Created by rayboot on 15/6/5.
 */
public class Base64Utils {

    // 将 s 进行 BASE64 编码
    public static String getBASE64(String s) {
        if (s == null) return null;
        return (new sun.misc.BASE64Encoder()).encode(s.getBytes());
    }

    // 将 BASE64 编码的字符串 s 进行解码
    public static String getFromBASE64(String s) {
        if (s == null) return null;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            byte[] b = decoder.decodeBuffer(s);
            return new String(b);
        } catch (Exception e) {
            return null;
        }
    }


    public static void main(String[] args) throws IOException {
        String key = "application/zip";
        System.out.println("加密结果为：" + getBASE64(key));
        System.out.println("加密耗时:" + getFromBASE64(getBASE64(key)));
    }
}