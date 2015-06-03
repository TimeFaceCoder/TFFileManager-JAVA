package cn.timeface.filemanager.uploadmanager;

import java.util.concurrent.ThreadFactory;

/**
 * Created by rayboot on 15/5/29.
 */
public class ManagerTreadFactory implements ThreadFactory {
    private final String mThreadName;

    public ManagerTreadFactory(String threadName) {
        mThreadName = threadName;
    }

    public ManagerTreadFactory() {
        this(null);
    }

    public Thread newThread(final Runnable r) {
        if (null != mThreadName) {
            return new Thread(r, mThreadName);
        } else {
            return new Thread(r);
        }
    }
}
