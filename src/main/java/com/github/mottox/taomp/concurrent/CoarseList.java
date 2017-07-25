package com.github.mottox.taomp.concurrent;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * TAOMP书中一种粗粒度同步List，每一个方法都必须获取内部的锁，最大的有点是显而易见的正确性。由于所有方法都必须获取锁才能对链表操作，
 * 所以执行实际上是串行的。
 * <p>
 * 它满足和内部锁相同的演进条件，如果内部锁时无饥饿的，则实现也是无饥饿的。
 * 如果竞争不激烈，则该类也是实现链表的一种很好的方式；
 * 如果竞争激烈，即便锁本身非常好，线程也会延迟等待其他线程。
 */
public class CoarseList<T> implements ConcurrentList<T> {

    private Node<T> head;

    private Lock lock = new ReentrantLock();

    public CoarseList() {
        head = new Node<>(Integer.MIN_VALUE);
        head.next = new Node<>(Integer.MAX_VALUE);
    }

    @Override
    public boolean add(T item) {
        Node<T> pred, curr;
        int key = item.hashCode();
        lock.lock();
        try {
            pred = head;
            curr = pred.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            if (key == curr.key) {
                return false;
            } else {
                Node<T> node = new Node<>(item);
                node.next = curr;
                pred.next = node;
                return true;
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean remove(T item) {
        Node<T> pred, curr;
        int key = item.hashCode();
        lock.lock();
        try {
            pred = head;
            curr = pred.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            if (key == curr.key) {
                pred.next = curr.next;
                // 辅助GC
                curr.next = null;
                return true;
            } else {
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    private static class Node<T> {
        T item;
        int key;
        Node<T> next;

        Node(int key) {
            this.key = key;
        }

        Node(T item) {
            this.item = item;
            this.key = item.hashCode();
        }
    }
}
