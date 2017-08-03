package com.github.mottox.taomp.concurrent.locks;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 简单的读写锁实现。
 */
public class SimpleReadWriteLock implements ReadWriteLock {

    private volatile int readers;

    private volatile boolean writer;

    private Lock lock;

    private Condition condition;

    private SimpleLock readLock;

    private SimpleLock writeLock;

    public SimpleReadWriteLock() {
        this.readers = 0;
        this.writer = false;
        this.lock = new ReentrantLock();
        this.readLock = new ReadLock();
        this.writeLock = new WriteLock();
        this.condition = lock.newCondition();
    }

    @Override
    public Lock readLock() {
        return readLock;
    }

    @Override
    public Lock writeLock() {
        return writeLock;
    }

    private class ReadLock implements SimpleLock {
        @Override
        public void lock() {
            lock.lock();
            try {
                while (writer) {
                    condition.await();
                }
                readers++;
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
                readers--;
                if (readers == 0) {
                    condition.signalAll();
                }
            } finally {
                lock.unlock();
            }
        }
    }

    private class WriteLock implements SimpleLock {
        @Override
        public void lock() {
            lock.lock();
            try {
                while (readers > 0 || writer) {
                    condition.await();
                }
                writer = true;
            } catch (InterruptedException e) {
                // empty
            } finally {
                lock.unlock();
            }
        }

        @Override
        public void unlock() {
            // 原书上这里需要获取lock，个人认为可以不需要。
            lock.lock();
            try {
                writer = false;
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }
}
