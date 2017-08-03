package com.github.mottox.taomp.concurrent;

import org.junit.Test;

/**
 * {@link FineList}测试类。
 *
 * @author Robin Wang
 */
public class FineListTest {

    @Test
    public void testAdd() throws Exception {
        ConcurrentListTestHelper.testAdd(new FineList<>());
    }

    @Test
    public void testRemove() throws Exception {
        ConcurrentListTestHelper.testRemove(new FineList<>());
    }

}