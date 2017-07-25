package com.github.mottox.taomp.concurrent;

/**
 * 并发列表。
 *
 * @author Robin Wang
 */
public interface ConcurrentList<T> {

    boolean add(T item);

    boolean remove(T item);

}
