package cn.timeface.filemanager.utils;

/**
 * @author rayboot
 * @from 15/5/27 17:50
 * @TODO
 */
public class Constants {
    private static final String ROOT = "http://192.168.1.66:8080/tfupload/upload/";

    public static final String SINGLE_UPLOAD = ROOT + "uploadfile";
    public static final String CHECK = ROOT + "check";
    public static final String MK_BLOCK = ROOT + "mkblock";
    public static final String PUT_BLOCK = ROOT + "putblock";
    public static final String MK_FILE = ROOT + "mkfile";

    public static final int SINGLE_UPLOAD_MAX_SIZE = 1024 * 1024;  //1m
    public static final int CHUCK_SIZE = 256 * 1024; //256k
    public static final int BLOCK_SIZE = 4 * 1024 * 1024; //4m

}
