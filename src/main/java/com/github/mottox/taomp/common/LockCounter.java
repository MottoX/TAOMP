package com.github.mottox.taomp.common;

import com.github.mottox.taomp.concurrent.locks.SimpleLock;

/**
 * 基于{@link SimpleLock}实现互斥的计数器。
 *
 * @author Robin Wang
 */
public class LockCounter {
    private long value;
    private SimpleLock lock;

    public LockCounter(SimpleLock lock) {
        this.lock = lock;
    }

    public long get() {
        // 不想用锁，只是为了暴露取值API。
        return value;
    }

    public long getAndIncrement() {
        lock.lock();
        try {
            long temp = value;
            value = temp + 1;
            return temp;
        } finally {
            lock.unlock();
        }
    }
}
