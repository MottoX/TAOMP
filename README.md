# TAOMP
The Art Of Multiprocessor Programming (TAOMP)是由Maurice Herlihy以及Nir Shavit所著
的一本关于多线程编程的书籍。

这本书讲述了并发编程设计的思想及理论。讨论了经典的互斥问题，共享存储器的性质以及一些高并发数据结构的原理与实践。

本仓库是我在学习过程中基于书中的代码所写的Java程序示例，整体基于原书，并针对Java的特性稍作了一些修改。

## 目录

* 第二章
  *  [LockOne](src/main/java/com/github/mottox/taomp/concurrent/locks/LockOne.java)
  *  [LockTwo](src/main/java/com/github/mottox/taomp/concurrent/locks/LockTwo.java)
  *  [Peterson](src/main/java/com/github/mottox/taomp/concurrent/locks/Peterson.java)

* 第七章
  *  [TASLock](src/main/java/com/github/mottox/taomp/concurrent/locks/TASLock.java)
  *  [TTASLock](src/main/java/com/github/mottox/taomp/concurrent/locks/TTASLock.java)
  *  [BackoffLock](src/main/java/com/github/mottox/taomp/concurrent/locks/BackoffLock.java)
  *  [ALock](src/main/java/com/github/mottox/taomp/concurrent/locks/ALock.java)
  *  [CLHLock](src/main/java/com/github/mottox/taomp/concurrent/locks/CLHLock.java)
  *  [MCSLock](src/main/java/com/github/mottox/taomp/concurrent/locks/MCSLock.java)
  *  [TOLock](src/main/java/com/github/mottox/taomp/concurrent/locks/TOLock.java)

* 第八章
  * [LockedQueue](src/main/java/com/github/mottox/taomp/concurrent/LockedQueue.java)
  * [SimpleReadWriteLock](src/main/java/com/github/mottox/taomp/concurrent/locks/SimpleReadWriteLock.java)
  * [SimpleReentrantLock](src/main/java/com/github/mottox/taomp/concurrent/locks/SimpleReentrantLock.java)
  * [FifoReadWriteLock](src/main/java/com/github/mottox/taomp/concurrent/locks/FifoReadWriteLock.java)
  * [Semaphore](src/main/java/com/github/mottox/taomp/concurrent/Semaphore.java)

* 第九章
  * [CoarseList](src/main/java/com/github/mottox/taomp/concurrent/CoarseList.java)
  * [FineList](src/main/java/com/github/mottox/taomp/concurrent/FineList.java)
