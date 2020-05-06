package java.util.concurrent;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 例如：栏栅，所有线程到达Barrier状态，一起执行（公交旅游demo）
 * 基于：ReentrantLock
 * 共享锁
 */
public class CyclicBarrier {

    // 总共线程数
    private final int parties;
    // 已经执行的线程数
    private int count;
    private final Runnable barrierCommand;


    public CyclicBarrier(int parties) {
        this(parties, null);
    }
    public CyclicBarrier(int parties, Runnable barrierAction) {
        if (parties <= 0) throw new IllegalArgumentException();
        this.parties = parties;
        this.count = parties;
        this.barrierCommand = barrierAction;
    }

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition trip = lock.newCondition();
    // 处理完，就会重新初始化下一代....
    private Generation generation = new Generation();

    private static class Generation {
        boolean broken = false;
    }
    // 下一代
    private void nextGeneration() {
        trip.signalAll();
        count = parties;
        generation = new Generation();
    }


    // 冲破栏栅（reset值）
    private void breakBarrier() {
        generation.broken = true;
        count = parties;
        trip.signalAll();
    }

    private int dowait(boolean timed, long nanos) throws InterruptedException, BrokenBarrierException, TimeoutException {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            final Generation g = generation;
            // 已经冲破
            if (g.broken){
                throw new BrokenBarrierException();
            }
            // 线程中断
            if (Thread.interrupted()) {
                breakBarrier();
                throw new InterruptedException();
            }

            int index = --count;
            // 所有线程都已经处理完，设置g.broken=true(出口)
            if (index == 0) {
                boolean ranAction = false;
                try {
                    final Runnable command = barrierCommand;
                    if (command != null){
                        command.run();
                    }
                    ranAction = true;
                    // **关键出口：最后一个线程index=0，会新建下一代
                    nextGeneration();
                    return 0;
                } finally {
                    if (!ranAction){
                        breakBarrier();
                    }
                }
            }

            // 自旋直到（冲破，线程中断，超时）
            for (;;) {
                try {
                    if (!timed){
                        trip.await();
                    }
                    else if (nanos > 0L){
                        nanos = trip.awaitNanos(nanos);
                    }
                } catch (InterruptedException ie) {
                    if (g == generation && ! g.broken) {
                        breakBarrier();
                        throw ie;
                    } else {
                        Thread.currentThread().interrupt();
                    }
                }

                // 冲破
                if (g.broken){
                    throw new BrokenBarrierException();
                }

                // **关键出口：最后一个线程index=0，会新建下一代
                if (g != generation){
                    return index;
                }

                // 超时
                if (timed && nanos <= 0L) {
                    breakBarrier();
                    throw new TimeoutException();
                }
            }
        } finally {
            lock.unlock();
        }
    }



    public int getParties() {
        return parties;
    }

    public int await() throws InterruptedException, BrokenBarrierException {
        try {
            return dowait(false, 0L);
        } catch (TimeoutException toe) {
            throw new Error(toe); // cannot happen
        }
    }

    public int await(long timeout, TimeUnit unit)
        throws InterruptedException,
               BrokenBarrierException,
               TimeoutException {
        return dowait(true, unit.toNanos(timeout));
    }

    public boolean isBroken() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return generation.broken;
        } finally {
            lock.unlock();
        }
    }

    public void reset() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            breakBarrier();   // break the current generation
            nextGeneration(); // start a new generation
        } finally {
            lock.unlock();
        }
    }


    public int getNumberWaiting() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return parties - count;
        } finally {
            lock.unlock();
        }
    }
}
