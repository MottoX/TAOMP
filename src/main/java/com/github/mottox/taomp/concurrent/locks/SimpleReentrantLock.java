package com.github.mottox.taomp.concurrent.locks;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.github.mottox.taomp.common.ThreadID;

/**
 * 简单的可重入锁。
 */
public class SimpleReentrantLock implements SimpleLock {

    private Lock lock;

    private Condition condition;

    private volatile int owner;

    private volatile int holdCount;

    public SimpleReentrantLock() {
        // 只是借用java.util.concurrent.ReentrantLock来实现互斥。
        this.lock = new ReentrantLock();
        this.condition = lock.newCondition();
        this.owner = -1;
        this.holdCount = 0;
    }

    @Override
    public void lock() {
        int me = ThreadID.get();
        lock.lock();
        try {
            if (owner == me) {
                holdCount++;
                return;
            }
            while (holdCount != 0) {
                condition.await();
            }
            owner = me;
            holdCount = 1;
        } catch (InterruptedException e) {
            // empty
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void unlock() {
        lock.lock();
        try {
            if (holdCount == 0 || owner != ThreadID.get()) {
                throw new IllegalMonitorStateException();
            }
            holdCount--;
            if (holdCount == 0) {
                condition.signal();
            }
        } finally {
            lock.unlock();
        }
    }

}
