package com.github.mottox.taomp.concurrent.locks;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * 锁测试套件类。
 *
 * @author Robin Wang
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ALockTest.class,
        BackoffLockTest.class,
        CLHLockTest.class,
        MCSLockTest.class,
        TASLockTest.class,
        TTASLockTest.class,
        TOLockTest.class})
public class LockTestSuite {
}
