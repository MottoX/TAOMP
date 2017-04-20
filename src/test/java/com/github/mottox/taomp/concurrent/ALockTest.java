package com.github.mottox.taomp.concurrent;

import org.junit.Test;

/**
 * {@link ALock}测试类。
 *
 * @author Robin Wang
 */
public class ALockTest {

    @Test(timeout = 2500)
    public void testALock() throws Exception {
        LockTestHelper.testFunctionality(new ALock(8));
    }
}