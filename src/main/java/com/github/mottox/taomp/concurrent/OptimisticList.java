package com.github.mottox.taomp.concurrent;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 乐观同步List。在遍历的时候不对节点上锁，而是到达目标节点时再进行上锁。
 * 因此遍历的时候可能会遍历到已经被删除的节点，但是节点在被删除的时候next不会变，所以仍然能够走回链表中，因此可以说该算法是无干扰的。
 * 但是该list不是无饥饿的，如果不断地添加删除节点，线程可能会被永久阻塞。但由于很少发生，所以该算法有比较好的实际效果。
 */
public class OptimisticList<T> implements ConcurrentList<T> {

    private final Node<T> head;

    public OptimisticList() {
        head = new Node<>(Integer.MIN_VALUE);
        head.next = new Node<>(Integer.MAX_VALUE);
    }

    @Override
    public boolean add(T item) {
        // 与FineList中的add不同的地方在于，OptimisticList在遍历的时候不需要获取节点的锁，而是在准备要加入节点的时候获取交叉耦合锁。
        int key = item.hashCode();
        while (true) {
            Node<T> pred = head;
            Node<T> curr = pred.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            pred.lock();
            curr.lock();

            try {
                /*
                 * 只要从head能走到pred+curr，则说明这两个节点在列表中仍然是有效的。
                 * 由于已经对pred和curr上锁，所以这两个节点此时不会受其他线程影响，仍然将在list中保持有效。
                 * 这里的验证操作是必须的，用于验证可达性。当然这也是OptimisticList低效的一个原因，这里相当于遍历两次链表了。
                 */
                if (validate(pred, curr)) {
                    if (curr.key == key) {
                        return false;
                    } else {
                        Node<T> node = new Node<>(item);
                        node.next = curr;
                        pred.next = node;
                        return true;
                    }
                }
            } finally {
                pred.unlock();
                curr.unlock();
            }
        }
    }

    @Override
    public boolean remove(T item) {
        int key = item.hashCode();
        while (true) {
            Node<T> pred = head;
            Node<T> curr = pred.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            pred.lock();
            curr.lock();

            try {
                if (validate(pred, curr)) {
                    if (curr.key == key) {
                        pred.next = curr.next;
                        return true;
                    } else {
                        return false;
                    }
                }
            } finally {
                pred.unlock();
                curr.unlock();
            }
        }
    }

    /**
     * 判断item是否存在于列表中。
     * 具体实现与add和remove类似。
     *
     * @param item 数据
     *
     * @return 存在返回true，否则返回false
     */
    public boolean contains(T item) {
        int key = item.hashCode();
        while (true) {
            Node<T> pred = head;
            Node<T> curr = pred.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            pred.lock();
            curr.lock();
            try {
                if (validate(pred, curr)) {
                    return curr.key == key;
                }
            } finally {
                pred.unlock();
                curr.unlock();
            }
        }
    }

    /**
     * 验证pred和curr在节点中有效，即未被删除，并且pred.next=curr
     *
     * @param pred 前驱节点
     * @param curr 当前节点
     *
     * @return 是否有效
     */
    private boolean validate(Node<T> pred, Node<T> curr) {
        Node<T> node = head;
        while (node.key <= pred.key) {
            if (node == pred) {
                return pred.next == curr;
            }
            node = node.next;
        }
        return false;
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
