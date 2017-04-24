package com.github.mottox.taomp.concurrent.locks;

/**
 * TAOMP中简化版的{@link java.util.concurrent.locks.Lock}。
 */
public interface SimpleLock {

    /**
     * 在进入临界区之前调用。
     */
    void lock();

    /**
     * 在离开临界区之前调用。
     */
    void unlock();

}
