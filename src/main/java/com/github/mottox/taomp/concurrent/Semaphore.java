package com.github.mottox.taomp.concurrent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 一种简单的信号量实现。
 */
public class Semaphore {

    private final int capacity;

    private volatile int state;

    private Lock lock;

    private Condition condition;

    public Semaphore(int capacity) {
        this.capacity = capacity;
        state = 0;
        lock = new ReentrantLock();
        condition = lock.newCondition();
    }

    public void acquire() {
        lock.lock();
        try {
            while (state == capacity) {
                condition.await();
            }
            state++;
        } catch (InterruptedException e) {
            // empty
        } finally {
            lock.unlock();
        }
    }

    public void release() {
        lock.lock();
        try {
            state--;
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
