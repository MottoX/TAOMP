package com.github.mottox.taomp.concurrent.locks;

import java.util.concurrent.atomic.AtomicBoolean;

import com.github.mottox.taomp.common.ThreadID;

/**
 * LockOne算法。
 * LockOne算法满足互斥性,至多只有一个线程可以进入临界区。
 * LockOne的缺陷在于当两个线程同时完成置flag为true操作，等待另一个线程的flag为false时，将发生死锁。
 */
public class LockOne implements SimpleLock {

    private AtomicBoolean[] flag = new AtomicBoolean[2];

    public LockOne() {
        flag[0] = new AtomicBoolean(false);
        flag[1] = new AtomicBoolean(false);
    }

    @Override
    public void lock() {
        int i = ThreadID.get();
        int j = 1 - i;
        flag[i].set(true);

        // 当两个线程都执行完上面的置flag操作时，等待另一个线程unlock置flag为false时，会发生死锁。
        while (flag[j].get()) {
        }
    }

    @Override
    public void unlock() {
        int i = ThreadID.get();
        flag[i].set(false);
    }
}
