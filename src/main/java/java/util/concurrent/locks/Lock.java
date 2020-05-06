package java.util.concurrent.locks;
import java.util.concurrent.TimeUnit;

public interface Lock {
    void lock();
    // 可中断加锁，获取过程中，不处理中断状态
    void lockInterruptibly() throws InterruptedException;
    boolean tryLock();
    boolean tryLock(long time, TimeUnit unit) throws InterruptedException;
    void unlock();


    // Condition对象的创建，供调用者添加不同的条件队列，实现更灵活多样化的等待机制
    Condition/*条件队列*/ newCondition();
}
