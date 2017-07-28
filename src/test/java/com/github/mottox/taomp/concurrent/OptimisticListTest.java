package com.github.mottox.taomp.concurrent;

import org.junit.Test;

/**
 * {@link OptimisticList}测试类。
 *
 * @author Robin Wang
 */
public class OptimisticListTest {

    @Test
    public void testAdd() throws Exception {
        ConcurrentListTestHelper.testAdd(new OptimisticList<>());
    }

    @Test
    public void testRemove() throws Exception {
        ConcurrentListTestHelper.testRemove(new OptimisticList<>());
    }

}