package cn.timeface.filemanager.beans;

/**
 * Created by rayboot on 15/5/29.
 */
public class BaseResponse {
    private String error;
    private int code;

    public void setError(String error) {
        this.error = error;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public int getCode() {
        return code;
    }
}
