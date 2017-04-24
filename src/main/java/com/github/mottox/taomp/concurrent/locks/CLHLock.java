package com.github.mottox.taomp.concurrent.locks;

import java.util.concurrent.atomic.AtomicReference;

/**
 * CLH队列锁。
 * 与{@link ALock}相比，CLHLock本身的空间复杂度是与最大并发线程无关的。在L把锁，n个线程，每个线程最多访问一把锁的情况下，空间复杂度为O(L+n)。
 * 原理：用一个tail原子引用维护一个虚拟队列，每个请求获得CLH锁的线程将自己的QNode中的锁标志位置为true，表示试图或者已经进入临界区。通过tail拿到
 * 前驱节点的QNode，检测前驱节点是否释放了锁，如果没有则旋转等待。释放锁则只需将当前持有的QNode节点锁标志位写为false,并复用前驱节点的QNode。
 * 因为维护了这样一个等待队列，CLH队列锁是公平的。
 * <p>
 * CLH锁的优点在于：锁的释放只会使后继节点的cache无效。
 */
public class CLHLock implements SimpleLock {

    /**
     * 队列尾节点。
     */
    private AtomicReference<QNode> tail;

    /**
     * 记录前驱节点。
     */
    private ThreadLocal<QNode> myPred;

    /**
     * 用于表示当前线程是否占有或者正在等待锁，只要未出临界区，都为true。
     */
    private ThreadLocal<QNode> myNode;

    public CLHLock() {
        this.tail = new AtomicReference<>(new QNode());
        this.myPred = new ThreadLocal<>();
        this.myNode = ThreadLocal.withInitial(QNode::new);
    }

    @Override
    public void lock() {
        QNode qNode = myNode.get();
        qNode.locked = true;
        // 将tail节点置为thread-local中的节点
        QNode pred = tail.getAndSet(qNode);
        // 拿到队列中前驱节点
        myPred.set(pred);
        while (pred.locked) {
        }
    }

    @Override
    public void unlock() {
        QNode qNode = myNode.get();
        qNode.locked = false;
        /*
         * 前驱节点的QNode已经没用，但可以被当前线程复用
         * 之所以有必要这样做，可以举例来说明。
         * 如果没有使用前驱节点的逻辑，在一个线程的情况下，当lock->unlock->lock的情况下，会出现pred==qNode的情况，无法获取锁。
         *
         * 而对于多线程，则可能出现死锁，以双线程为例：
         * t1获取锁 qNode1(true)
         * t2等待锁 qNode1(true) <- qNode2(true)
         * t1释放锁 qNode1(false) <- qNode2(true)
         * t1立即再尝试获取锁 qNode1(true) <- qNode2(true) <- qNode1(true)
         * qNode1和qNode互为前驱，并且locked都为true，出现死锁。
         */
        myNode.set(myPred.get());
    }

    private static class QNode {
        private volatile boolean locked = false;
    }
}

