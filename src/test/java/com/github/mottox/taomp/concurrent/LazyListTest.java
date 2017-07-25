package com.github.mottox.taomp.concurrent;

import org.junit.Test;

/**
 * {@link LazyList}测试类。
 *
 * @author Robin Wang
 */
public class LazyListTest {

    @Test
    public void testAdd() throws Exception {
        ConcurrentListTestHelper.testAdd(new LazyList<>());
    }

    @Test
    public void testRemove() throws Exception {
        ConcurrentListTestHelper.testRemove(new LazyList<>());
    }

}