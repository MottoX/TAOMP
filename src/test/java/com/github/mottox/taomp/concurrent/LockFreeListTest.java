package com.github.mottox.taomp.concurrent;

import org.junit.Test;

/**
 * {@link LockFreeList}测试类。
 *
 * @author Robin Wang
 */
public class LockFreeListTest {

    @Test
    public void testAdd() throws Exception {
        ConcurrentListTestHelper.testAdd(new LockFreeList<>());

    }

    @Test
    public void testRemove() throws Exception {
        ConcurrentListTestHelper.testRemove(new LockFreeList<>());
    }

}