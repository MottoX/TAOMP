package com.github.mottox.taomp.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程ID辅助类，用于获取线程ID。
 *
 * @author Robin Wang
 */
public class ThreadID {

    private static Map<Long, Integer> threadIDs = new ConcurrentHashMap<>();

    private static AtomicInteger counter = new AtomicInteger();

    public static int get() {
        long id = Thread.currentThread().getId();
        return threadIDs.computeIfAbsent(id, key -> counter.getAndIncrement());
    }
}
