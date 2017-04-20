package com.github.mottox.taomp.concurrent;

import org.junit.Test;

/**
 * {@link MCSLock}测试类。
 *
 * @author Robin Wang
 */
public class MCSLockTest {

    @Test(timeout = 500)
    public void testMCSLock() throws Exception {
        LockTestHelper.testFunctionality(new MCSLock());
    }
}