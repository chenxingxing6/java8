package java.util.concurrent;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * 比如：主线程等待所有子线程都执行完，再执行（老师监考demo）
 * 基于AQS
 * 共享锁Shared
 */
public class CountDownLatch {
    private final Sync sync;

    // 构造函数，初始化总线程数
    public CountDownLatch(int count) {
        if (count < 0) throw new IllegalArgumentException("count < 0");
        this.sync = new Sync(count);
    }

    // 核心方法，每个线程执行完count-1
    public void countDown() {
        sync.releaseShared(1);
    }

    // 一直自旋，结束条件（count=0）
    public void await() throws InterruptedException {
        sync.acquireSharedInterruptibly(1);
    }


    // 静态内部类
    private static final class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 4982264981922014374L;
        Sync(int count) {
            setState(count);
        }
        int getCount() {
            return getState();
        }
        protected int tryAcquireShared(int acquires) {
            return (getState() == 0) ? 1 : -1;
        }

        protected boolean tryReleaseShared(int releases) {
            for (;;) {
                int c = getState();
                if (c == 0){
                    return false;
                }
                int nextc = c-1;
                if (compareAndSetState(c, nextc)){
                    return nextc == 0;
                }
            }
        }
    }


    public boolean await(long timeout, TimeUnit unit)
        throws InterruptedException {
        return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
    }

    public long getCount() {
        return sync.getCount();
    }

    public String toString() {
        return super.toString() + "[Count = " + sync.getCount() + "]";
    }
}
