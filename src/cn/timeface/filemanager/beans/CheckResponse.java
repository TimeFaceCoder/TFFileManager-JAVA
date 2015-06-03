package cn.timeface.filemanager.beans;

import java.util.List;

/**
 * @author rayboot
 * @from 15/5/28 11:17
 * @TODO
 */
public class CheckResponse extends BaseResponse {
    List<String> checksum;


    public List<String> getChecksum() {
        return checksum;
    }

    public void setChecksum(List<String> checksum) {
        this.checksum = checksum;
    }
}
