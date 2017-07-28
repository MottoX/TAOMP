package com.github.mottox.taomp.concurrent;

import java.util.concurrent.atomic.AtomicMarkableReference;

/**
 * 无锁队列。
 * 一种可以无锁支持并发插入和删除的方法是保证节点在逻辑或者物理删除时，该节点的域（如next）不能被修改。
 * 本章讨论的其他有锁队列通过互斥锁保证了节点在被逻辑/物理删除后，不会对该节点进行任何修改。
 * 如果需要使用一种无锁的实现的话，可以将节点的next域和删除标志位marked封装为单个原子单位。
 */
public class LockFreeList<T> implements ConcurrentList<T> {

    private final Node<T> head;
    private final Node<T> tail;

    public LockFreeList() {
        head = new Node<>(Integer.MIN_VALUE);
        tail = new Node<>(Integer.MAX_VALUE);
        head.next = new AtomicMarkableReference<>(tail, false);
        tail.next = new AtomicMarkableReference<>(null, false);
    }

    @Override
    public boolean add(T item) {
        int key = item.hashCode();
        while (true) {
            Window window = find(head, key);
            Node<T> pred = window.pred, curr = window.curr;
            if (curr.key == key) {
                return false;
            } else {
                Node<T> node = new Node<>(item);
                node.next = new AtomicMarkableReference<>(curr, false);
                if (pred.next.compareAndSet(curr, node, false, false)) {
                    return true;
                }
            }
        }
    }

    @Override
    public boolean remove(T item) {
        int key = item.hashCode();
        boolean snip;
        while (true) {
            Window window = find(head, key);
            Node<T> pred = window.pred, curr = window.curr;
            if (curr.key != key) {
                return false;
            } else {
                Node<T> succ = curr.next.getReference();
                // 标记当前节点被逻辑删除,如果cas失败需要重试
                snip = curr.next.compareAndSet(succ, succ, false, true);
                if (!snip) {
                    continue;
                }
                pred.next.compareAndSet(curr, succ, false, false);
                return true;
            }
        }
    }

    private static class Node<T> {
        T item;
        int key;
        AtomicMarkableReference<Node<T>> next;

        Node(int key) {
            this.key = key;
        }

        Node(T item) {
            this.item = item;
            this.key = item.hashCode();
        }

    }

    private Window find(Node<T> head, int key) {
        Node<T> pred, curr, succ;
        boolean[] marked = {false};
        boolean snip;
        retry:
        while (true) {
            pred = head;
            curr = pred.next.getReference();
            while (true) {
                succ = curr.next.get(marked);
                // 向后找到第一个没有被逻辑删除的节点
                while (marked[0]) {
                    snip = pred.next.compareAndSet(curr, succ, false, false);
                    if (!snip) {
                        continue retry;
                    }
                    curr = succ;
                    succ = curr.next.get(marked);
                }
                if (curr.key >= key) {
                    return new Window(pred, curr);
                }
                pred = curr;
                curr = succ;
            }
        }
    }

    private class Window {
        Node<T> pred;
        Node<T> curr;

        Window(Node<T> pred, Node<T> curr) {
            this.pred = pred;
            this.curr = curr;
        }

    }

}
