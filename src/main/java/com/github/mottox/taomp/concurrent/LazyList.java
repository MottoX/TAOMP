package com.github.mottox.taomp.concurrent;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 惰性同步list。相比{@link OptimisticList}在#add(),#remove()等方法需要遍历两遍，惰性同步在此有了无锁的实现。
 * 它通过给每个节点新增一个Boolean类型的标记来表示节点是否还在链表中，从而利用【所有未标记的节点都是可达的】这样一个不变式
 * 来消除重复遍历验证。对于#remove()方法，通过标记线进行逻辑删除，再通过修改前驱的next域进行物理删除。
 */
public class LazyList<T> implements ConcurrentList<T> {

    private Node<T> head;

    public LazyList() {
        head = new Node<>(Integer.MIN_VALUE);
        head.next = new Node<>(Integer.MAX_VALUE);
    }

    @Override
    public boolean add(T item) {
        int key = item.hashCode();
        while (true) {
            Node<T> pred = this.head;
            Node<T> curr = head.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            pred.lock();
            try {
                curr.lock();
                try {
                    if (validate(pred, curr)) {
                        if (curr.key == key) {
                            return false;
                        }
                        Node<T> node = new Node<>(item);
                        node.next = curr;
                        pred.next = node;
                        return true;
                    }
                } finally {
                    curr.unlock();
                }
            } finally {
                pred.unlock();
            }
        }
    }

    @Override
    public boolean remove(T item) {
        int key = item.hashCode();
        while (true) {
            Node<T> pred = this.head;
            Node<T> curr = head.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            pred.lock();
            try {
                curr.lock();
                try {
                    if (validate(pred, curr)) {
                        if (curr.key != key) {
                            return false;
                        }
                        curr.marked = true;
                        pred.next = curr.next;
                        return true;
                    }
                } finally {
                    curr.unlock();
                }
            } finally {
                pred.unlock();
            }
        }
    }

    private boolean validate(Node<T> pred, Node<T> curr) {
        // pred和curr必须都是未标记的（可达的）并且pred.next == curr需要成立
        return !pred.marked && !curr.marked && pred.next == curr;
    }

    private static class Node<T> {
        T item;
        int key;
        Node<T> next;
        boolean marked;
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
