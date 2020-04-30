package java.util.concurrent.locks;
import java.util.concurrent.TimeUnit;
import java.util.Date;

/**
 * Condition的使用必须在lock()和unlock()方法之间
 */
public interface Condition {

    // 使当前线程等待，释放锁
    void await() throws InterruptedException;

    void awaitUninterruptibly();

    long awaitNanos(long nanosTimeout) throws InterruptedException;

    boolean await(long time, TimeUnit unit) throws InterruptedException;

    boolean awaitUntil(Date deadline) throws InterruptedException;

    // 唤醒线程
    void signal();

    void signalAll();
}
