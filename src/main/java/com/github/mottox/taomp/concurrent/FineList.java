package com.github.mottox.taomp.concurrent;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 与{@link CoarseList}不同，FineList使用了一种细粒度的锁机制。FineList算法是无饥饿的。
 */
public class FineList<T> implements ConcurrentList<T> {

    private Node<T> head;

    public FineList() {
        head = new Node<>(Integer.MIN_VALUE);
        head.next = new Node<>(Integer.MAX_VALUE);
    }

    /**
     * 向列表中加入一个节点。
     * FineList使用的是细粒度锁，也即对列表中某个节点进行修改（新增/删除/修改）时，除了获取节点本身的锁，还需要获得前驱节点的锁。
     * 注意，add方法与remove获取节点锁的次序应该一致，如果不一致的话会导致死锁的情况发生。
     * 例如：head -> a -> c, A线程试图在a和c之间插入b，B线程试图删除c节点，如果A线程获取锁的顺序为a + c而B线程试图获取锁的顺序为c + a，
     * 则有相当大的可能发生死锁。
     *
     * @param item 待加入的数据
     *
     * @return 成功加入返回true，否则返回false
     */
    @Override
    public boolean add(T item) {
        int key = item.hashCode();
        head.lock();
        Node<T> pred = head;
        try {
            Node<T> curr = pred.next;
            curr.lock();
            try {
                while (curr.key < key) {
                    pred.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock();
                }
                if (curr.key == key) {
                    return false;
                }
                Node<T> newNode = new Node<>(item);
                newNode.next = curr;
                pred.next = newNode;
                return true;
            } finally {
                curr.unlock();
            }
        } finally {
            pred.unlock();
        }
    }

    /**
     * 从列表中删除一个节点。
     * 之所以FineList在需要同时获取前驱节点和当前节点的锁可以考虑下面的场景：
     * head -> a -> b -> c
     * 如果有两个线程A和B分别想要删除a节点和b节点，A线程仅持有a锁，B线程仅持有b锁，
     * A线程：head.next = b
     * B线程：a.next = c
     * 效果相当于只有a节点被删除,问题在于A线程删除a后,B线程读到的前驱a实际上已经无效了。
     *
     * @param item 待删除数据
     *
     * @return 成功删除返回true，否则返回false
     */
    @Override
    public boolean remove(T item) {
        Node<T> pred = null, curr = null;
        int key = item.hashCode();
        head.lock();
        try {
            pred = head;
            curr = pred.next;
            curr.lock();
            try {
                while (curr.key < key) {
                    pred.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock();
                }
                if (curr.key == key) {
                    pred.next = curr.next;
                    // 辅助GC
                    curr.next = null;
                    return true;
                }
                return false;
            } finally {
                curr.unlock();
            }
        } finally {
            pred.unlock();
        }
    }

    private static class Node<T> {
        T item;
        int key;
        Node<T> next;
        Lock lock = new ReentrantLock();

        Node(int key) {
            this.key = key;
        }

        Node(T item) {
            this.item = item;
            this.key = item.hashCode();
        }

        void lock() {
            lock.lock();
        }

        void unlock() {
            lock.unlock();
        }
    }
}
