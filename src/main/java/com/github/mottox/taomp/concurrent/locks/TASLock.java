package com.github.mottox.taomp.concurrent.locks;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 一种基于testAndSet()指令的锁算法。
 */
public class TASLock implements SimpleLock {

    private AtomicBoolean state = new AtomicBoolean(false);

    /**
     * 在TAOMP中有这样的段落来描述为什么TASLock效率低：
     * 每个getAndSet（）调用实质上是总线上的一个广播。由于所有线程都必须通过总线和内存进行通信，所以getAndSet()调用将会延迟所有的线程，
     * 包括哪些没有等待锁的线程。更为糟糕的是，getAndSet()调用能迫使其他的处理器丢弃他们自己cache中的锁副本，这样每一个正在自选的线程
     * 几乎每次都会遇到一个cache确实，必须通过总线来来获取新的没有被修改的值。而比这更为糟糕的是当持有锁的线程试图释放锁时，由于总线被正在
     * 自旋的线程所独占，该线程有可能会被延迟。
     */
    @Override
    public void lock() {
        // 只有当state为false时才可以获取到锁
        while (state.getAndSet(true)) {
        }
    }

    @Override
    public void unlock() {
        state.set(false);
    }
}
