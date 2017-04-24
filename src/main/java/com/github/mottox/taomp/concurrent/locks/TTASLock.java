package com.github.mottox.taomp.concurrent.locks;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 与{@link TASLock}效果等价的基于testAndGet()的锁算法。
 */
public class TTASLock implements SimpleLock {

    private AtomicBoolean state = new AtomicBoolean(false);

    /**
     * 相比{@link TASLock#lock()}的实现，TTASLock的lock方法实现加上了判断是否当前锁被占用，避免每次都直接执行getAndSet。
     * TTASLock的有点在于尝试获取锁的线程不会产生总线流量，降低其他线程的内存访问速度。释放锁的线程也不会被在锁上旋转的线程所延迟。
     * 然而，当锁被释放时情况却不理想，锁的持有者将false值写入锁变量来释放所，该操作会使自旋线程的cache副本立刻失效。每个线程都将发生一次
     * cache缺失并重读新值。它们都（几乎是同时）调用getAndSet()获取锁。第一个成功的线程将使其他线程失效，这些失效线程接下来又重新读取那个值，
     * 从而引起一场总线流量风暴。最终，所有线程再次平静，进入本地旋转。
     */
    @Override
    public void lock() {
        while (true) {
            while (state.get()) {
            }
            if (!state.getAndSet(true)) {
                return;
            }
        }
    }

    @Override
    public void unlock() {
        state.set(false);
    }

}

