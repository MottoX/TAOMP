package com.github.mottox.taomp.concurrent;

import java.util.concurrent.atomic.AtomicReferenceArray;

import com.github.mottox.taomp.common.ThreadID;
import com.github.mottox.taomp.locks.Lock;

/**
 * Peterson锁，一种{@link LockOne}和{@link LockTwo}结合起来的无饥饿锁算法。
 * 在TAOMP中将其称为最简洁与最完美的双线程互斥算法，以其发明者名字命名。
 */
public class Peterson implements Lock {

    private AtomicReferenceArray<Boolean> flag = new AtomicReferenceArray<>(2);

    private volatile int victim;

    public Peterson() {
        flag.set(0, false);
        flag.set(1, false);
    }

    @Override
    public void lock() {
        int i = ThreadID.get();
        int j = 1 - i;

        // 置flag为true表明试图进入临界区
        flag.set(i, true);
        // 礼让另一个线程，自己作为牺牲者
        victim = i;

        /* 当另一个线程试图进入临界区，并且自己是牺牲者时，等待。
         * 换而言之，此时另一个线程已经进入临界区或者先于当前线程执行到此处时，
         * 当前线程需要礼让对方，自己陷入等待。而另一个线程即便还未进入临界区由于判断到victim不是自己，
         * 可以顺利进入临界区而不会发生死锁。（规避了LockOne的缺陷）。
         * 而在无竞争的情况下，即便自己是牺牲者线程，也可以判断到另一个线程flag为false，而不会陷入等待（规避了LockOne的缺陷）。
         */
        while (flag.get(j) && victim == i) {
        }
    }

    @Override
    public void unlock() {
        int i = ThreadID.get();
        flag.set(i, false);
    }
}
