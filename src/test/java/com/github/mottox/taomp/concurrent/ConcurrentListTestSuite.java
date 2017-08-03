package com.github.mottox.taomp.concurrent;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * 并发列表测试套件。
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        CoarseListTest.class,
        FineListTest.class,
        LazyListTest.class,
        OptimisticListTest.class,
        LockFreeListTest.class})
public class ConcurrentListTestSuite {
}
