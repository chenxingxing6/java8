package java.util.concurrent;

import java.util.Collection;
import java.util.Queue;

/**
 * 阻塞队列
 */
public interface BlockingQueue<E> extends Queue<E> {
    // 会自旋，直到插入
    void put(E e) throws InterruptedException;
    // 会自旋，直到获取
    E take() throws InterruptedException;


    // 抛异常
    boolean add(E e);

    // 返回false
    boolean offer(E e);

    boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException;

    E poll(long timeout, TimeUnit unit) throws InterruptedException;

    int remainingCapacity();

    boolean remove(Object o);

    public boolean contains(Object o);

    int drainTo(Collection<? super E> c);
    int drainTo(Collection<? super E> c, int maxElements);
}
