package com.github.mottox.taomp.concurrent.locks;

import org.junit.Test;

/**
 * {@link TASLock}测试类。
 *
 * @author Robin Wang
 */
public class TASLockTest {

    @Test
    public void testTASLock() throws Exception {
        LockTestHelper.testFunctionality(new TASLock());
    }
}