package com.github.mottox.taomp.concurrent;

import org.junit.Test;

/**
 * {@link CoarseList}测试类。
 *
 * @author Robin Wang
 */
public class CoarseListTest {

    @Test(timeout = 10000)
    public void testAdd() throws Exception {
        ConcurrentListTestHelper.testAdd(new CoarseList<>());
    }

    @Test(timeout = 10000)
    public void testRemove() throws Exception {
        ConcurrentListTestHelper.testRemove(new CoarseList<>());
    }

}