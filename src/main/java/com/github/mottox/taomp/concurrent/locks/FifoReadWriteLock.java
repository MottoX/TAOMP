package com.github.mottox.taomp.concurrent.locks;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 公平的读写锁。相比{@link SimpleReadWriteLock}，提供了保证写锁相比读锁具有优先级的机制。
 */
public class FifoReadWriteLock implements ReadWriteLock {

    private volatile int readAcquires;

    private volatile int readReleases;

    private volatile boolean writer;

    private Lock lock;

    private Condition condition;

    private SimpleLock readLock;

    private SimpleLock writeLock;

    public FifoReadWriteLock() {
        this.readAcquires = 0;
        this.readReleases = 0;
        this.writer = false;
        this.lock = new ReentrantLock(true);
        this.condition = lock.newCondition();
        this.readLock = new ReadLock();
        this.writeLock = new WriteLock();
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
                readAcquires++;
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
                readReleases++;
                // 最后一个读锁被释放，唤醒所有在condition上等待的线程。
                if (readAcquires == readReleases) {
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
                while (writer) {
                    condition.await();
                }
                /*
                 * 只要没有其它线程正在写，则置writer为true,此后不会再有新增的获得读锁的线程了。
                 * 后继的试图获得写锁的请求也会在condition上等待。再接下来该线程会等待所有当前读请求释放读锁。
                 */
                writer = true;
                while (readAcquires != readReleases) {
                    condition.await();
                }
            } catch (InterruptedException e) {
                // empty
            } finally {
                lock.unlock();
            }
        }

        @Override
        public void unlock() {
            writer = false;
            condition.signalAll();
        }
    }
}
