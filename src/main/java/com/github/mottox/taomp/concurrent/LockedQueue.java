package com.github.mottox.taomp.concurrent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用锁和条件的FIFO队列。
 * 这种将方法、互斥锁、条件对象组合在一起的整体称为管程。
 */
public class LockedQueue<T> {

    private final Lock lock = new ReentrantLock();

    private final Condition notFull = lock.newCondition();

    private final Condition notEmpty = lock.newCondition();

    private final T[] items;

    private int tail;

    private int head;

    private int count;

    @SuppressWarnings("unchecked")
    public LockedQueue(int capacity) {
        this.items = (T[]) new Object[capacity];
    }

    public void enq(T x) throws InterruptedException {
        lock.lock();
        try {
            while (count == items.length) {
                notFull.await();
            }
            items[tail] = x;
            if (++tail == items.length) {
                tail = 0;
            }
            ++count;

            /*
             * 书上举了个例子，如果我们在这行代码外面包上一个if (count == 1)的条件，就会导致唤醒丢失的问题。
             * 例如有两个线程A和B在notEmpty上阻塞，队列从空到非空的时候，线程A被唤醒，在A获得锁之前，如果生产者C又放了一个数据，这时候不会触发
             * notEmpty.signal()，B线程将继续阻塞，尽管明明可以被唤醒。
             */
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public T deq() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0) {
                notEmpty.await();
            }
            T x = items[head];
            if (++head == items.length) {
                head = 0;
            }
            --count;
            notFull.signal();
            return x;
        } finally {
            lock.unlock();
        }
    }
}
