# TAOMP
The Art Of Multiprocessor Programming (TAOMP)是由Maurice Herlihy以及Nir Shavit所著
的一本关于多线程编程的书籍。

这本书讲述了并发编程设计的思想及理论。讨论了经典的互斥问题，共享存储器的性质以及一些高并发数据结构的原理与实践。

本仓库是我在学习过程中基于书中的代码所写的Java程序示例，整体基于原书，并针对Java的特性稍作了一些修改。

## 目录

* 第二章
  *  [LockOne算法](src/main/java/com/github/mottox/taomp/concurrent/LockOne.java)
  *  [LockTwo算法](src/main/java/com/github/mottox/taomp/concurrent/LockTwo.java)
  *  [Peterson算法](src/main/java/com/github/mottox/taomp/concurrent/Peterson.java)
