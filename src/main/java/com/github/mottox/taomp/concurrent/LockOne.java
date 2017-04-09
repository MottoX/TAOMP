package com.github.mottox.taomp.concurrent;

import java.util.concurrent.atomic.AtomicReferenceArray;

import com.github.mottox.taomp.common.ThreadID;
import com.github.mottox.taomp.locks.Lock;

/**
 * LockOne算法。
 * LockOne算法满足互斥性,至多只有一个线程可以进入临界区。
 * LockOne的缺陷在于当两个线程同时完成置flag为true操作，等待另一个线程的flag为false时，将发生死锁。
 */
public class LockOne implements Lock {

    private AtomicReferenceArray<Boolean> flag = new AtomicReferenceArray<>(2);

    public LockOne() {
        flag.set(0, false);
        flag.set(1, false);
    }

    @Override
    public void lock() {
        int i = ThreadID.get();
        int j = 1 - i;
        flag.set(i, true);

        // 当两个线程都执行完上面的置flag操作时，等待另一个线unlock置flag为false时，会发生死锁。
        while (flag.get(j)) {
        }
    }

    @Override
    public void unlock() {
        int i = ThreadID.get();
        flag.set(i, false);
    }
}
