package com.github.mottox.taomp.concurrent.locks;

import com.github.mottox.taomp.common.ThreadID;

/**
 * LockTwo算法。
 * LockTwo算法满足互斥性，至多只有一个线程可以进入临界区。
 * LockTwo算法中有个非常有意思的变量--victim，即所谓的牺牲者。作为牺牲者的线程是无法进入临界区的，除非牺牲者变量变为其他线程。
 * LockTwo的缺陷在于在无竞争环境下，一个线程在试图进入临界区时会陷入无限等待。
 */
public class LockTwo implements SimpleLock {

    private volatile int victim;

    @Override
    public void lock() {
        int i = ThreadID.get();
        victim = i;

        // 作为牺牲者的线程是无法结束lock方法，进入临界区的。
        while (victim == i) {
        }
    }

    @Override
    public void unlock() {
        // empty
    }
}
