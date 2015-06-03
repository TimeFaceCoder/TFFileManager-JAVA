package cn.timeface.filemanager.uploadmanager;

/**
 * Created by rayboot on 15/5/29.
 */
public abstract class ManagerThreadRunnable implements Runnable {
    public final void run() {
//        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        runImpl();
    }

    public abstract void runImpl();

    protected boolean isInterrupted() {
        return Thread.currentThread().isInterrupted();
    }
}
