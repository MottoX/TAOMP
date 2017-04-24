package com.github.mottox.taomp.concurrent.locks;

import java.util.concurrent.atomic.AtomicReference;

/**
 * MCS队列锁。
 * MCS队列锁与{@link CLHLock}有异曲同工之处。
 * 优点在于释放锁的时候只会使后继线程cache无效，缺点则是释放锁的时候也可能会旋转等待。另外，读、写、CAS操作比{@link CLHLock}要多。
 */
public class MCSLock implements SimpleLock {

    private AtomicReference<QNode> tail;

    private ThreadLocal<QNode> myNode;

    public MCSLock() {
        this.tail = new AtomicReference<>(null);
        this.myNode = ThreadLocal.withInitial(QNode::new);
    }

    @Override
    public void lock() {
        QNode qNode = myNode.get();
        QNode pred = tail.getAndSet(qNode);
        if (pred != null) {
            qNode.locked = true;
            pred.next = qNode;
            while (qNode.locked) {
            }
        }
    }

    @Override
    public void unlock() {
        QNode qNode = myNode.get();
        if (qNode.next == null) {
            /* 检测当前线程持有的qNode是否为队列尾，并将队列尾置为null，如果CAS操作成功，则释放锁。
             * 之所以要将队列尾置为null，是为了保证后继线程通过tail读到的pred一定为null。
             */
            if (tail.compareAndSet(qNode, null)) {
                return;
            }
            // 等待后继线程执行pred.next = qNode
            while (qNode.next == null) {
            }
        }
        // 将后继线程的locked置为false，允许其进入
        qNode.next.locked = false;
        // 断开与后继线程的连接
        qNode.next = null;
    }

    private class QNode {
        private volatile boolean locked;
        private volatile QNode next;
    }
}
