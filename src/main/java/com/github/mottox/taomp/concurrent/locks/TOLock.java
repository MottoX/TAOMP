package com.github.mottox.taomp.concurrent.locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 时限锁。
 * 该锁实现了类似于JDK的Lock接口中的tryLock()方法，可以在请求锁时设定一个超时时间。
 * 在实现上，TOLock与普通互斥锁的区别在于，如果超时退出放弃锁，需要一种能够让后继线程感知到的机制，
 * <p>
 * 与其说TOLock中维护了一个等待列表，更不如说是一个等待链表，因为在超时的时候节点的行为类似于将自己从链表中删除，将后继线程与前驱线程在链表中串起来。
 * TOLock具有{@link CLHLock}大多数优点：在缓存的存储单元上进行本地自旋以及对锁空闲的快速检测，也有 {@link BackoffLock}的无等待超时特性。
 * 该锁的缺点在于，每次锁访问都需要分配一个新节点以及在访问临界区之前可能不得不回溯一个超时节点链。
 */
public class TOLock implements CompleteLock {

    private static final QNode AVAILABLE = new QNode();

    private AtomicReference<QNode> tail;

    private ThreadLocal<QNode> myNode;

    public TOLock() {
        this.tail = new AtomicReference<>(null);
        this.myNode = new ThreadLocal<>();
    }

    @Override
    public void lock() {
        QNode qNode = new QNode();
        myNode.set(qNode);
        qNode.pred = null;

        QNode myPred = tail.getAndSet(qNode);
        // 队列中无前驱节点，直接占有锁。
        if (myPred == null) {
            return;
        }

        QNode predPred;
        while ((predPred = myPred.pred) != AVAILABLE) {
            // 前驱节点可能超时退出，myPred以它（超时退出时）设置的pred为新前驱。
            if (predPred != null) {
                myPred = predPred;
            }
        }
    }

    @Override
    public void unlock() {
        QNode qNode = myNode.get();
        // 如果有其它后继争用线程，将当前节点的pred标记为可用，使其可以探测到锁已经被释放。
        if (!tail.compareAndSet(qNode, null)) {
            qNode.pred = AVAILABLE;
        }
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        long patience = TimeUnit.MILLISECONDS.convert(time, unit);

        QNode qNode = new QNode();
        myNode.set(qNode);
        qNode.pred = null;

        QNode myPred = tail.getAndSet(qNode);
        // 如果队列中无前驱（在低争用情况下未必是第一个）也即当前锁无线程持有，或者上一个节点已经释放锁，则成功持有锁。
        if (myPred == null || myPred.pred == AVAILABLE) {
            return true;
        }

        while (System.currentTimeMillis() - startTime < patience) {
            // 因为myPred可能会重新挂在到新前驱，所以predPred每次需要重读。
            QNode predPred = myPred.pred;
            /*
             * 如果前驱节点释放了锁，则成功占有锁,
             * 否则如果前驱节点（超时退出时）设置了它的前驱，
             * 则将当前节点的前驱挂到前驱的前驱。
             */
            if (predPred == AVAILABLE) {
                return true;
            } else if (predPred != null) {
                myPred = predPred;
            }
        }

        /*
         * 超时，尝试将tail指针CAS到前驱节点，
         * 在没有后继线程的情况下，这样可以使得队列中当前线程仿佛从没尝试占有过锁。
         * 而如果有后继线程，如果直接退出，会使得后继线程无法得知之后应该依靠哪个节点来判断是否可以占有锁，
         * 因此需要设置当前节点的myPred，使后继线程可以以此为依据。
         */
        if (!tail.compareAndSet(qNode, myPred)) {
            // 如果失败的话，将pred挂到前驱节点。
            qNode.pred = myPred;
        }
        return false;
    }

    private static class QNode {
        private volatile QNode pred;
    }
}
