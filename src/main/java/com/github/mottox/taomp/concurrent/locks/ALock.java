package com.github.mottox.taomp.concurrent.locks;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * 基于数组的简单队列锁，由Tom Anderson发明。
 * 每个线程在请求锁的时候，需要判断前一个线程是否释放锁来判断是否可以进入临界区。
 * 队列保证了锁的公平性。
 */
public class ALock implements SimpleLock {

    private ThreadLocal<Integer> mySlotIndex = ThreadLocal.withInitial(() -> 0);

    private AtomicInteger tail;

    /*
     * 书这里用的是volatile boolean[],虽然有说法volatile对于数组来说只是数组引用具有volatile含义的可见性，
     * 但经过实践，volatile在这里修饰数组引用，确实可以保证flag数组中元素的可见性，而去掉之后则会有问题。
     */
    private volatile boolean[] flag;

    private int size;

    /**
     * 构造一个ALock。
     * 注意容量大小应该保证至少为最大并发线程数。
     * 每个线程在请求锁的只有对应的flag值为true才可以进入临界区，否则等待。
     *
     * @param capacity 容量
     */
    public ALock(int capacity) {
        size = capacity;
        tail = new AtomicInteger(0);
        flag = new boolean[capacity];
        // 初始激活第一个slot
        for (int i = 0; i < capacity; i++) {
            flag[i] = i == 0;
        }
    }

    @Override
    public void lock() {
        // 从计数器中拿个号，映射到环形数组中的某个slot
        int slot = tail.getAndIncrement() % size;
        // 保存到ThreadLocal中
        mySlotIndex.set(slot);
        while (!flag[slot]) {
        }
    }

    @Override
    public void unlock() {
        int slot = mySlotIndex.get();
        flag[slot] = false;
        flag[(slot + 1) % size] = true;
    }
}
