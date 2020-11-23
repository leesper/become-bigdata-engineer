# YARN资源调度系统

## 知识点

* YARN分布式资源调度系统重要组件
  * 负责资源管理和分配的ResourceManager
  * 负责执行计算和监控节点资源的NodeManager
  * JobHistoryServer
  * Timeline Server
  * 调度器Scheduler
  * 应用程序管理器Applications Manager 
  * 资源分配抽象Container
  * ApplicationMaster
* YARN应用运行原理，应用提交过程
* MapReduce on YARN
  * 提交作业
  * 初始化作业
  * Task任务分配
  * Task任务执行
  * 作业运行进度与状态更新
  * 完成作业
  * YARN应用生命周期
* YARN调度器（Scheduler）
  * FIFO调度器
  * Fair调度器
  * Capacity调度器
* 自定义队列，任务提交到不同队列

## 思考题

1. YARN中有几种调度器？使用FairScheduler自定义队列大概经过几步？

答：YARN中有3种调度器：FIFO、Fair和Capacity。FIFO是按先后顺序分配资源，但大的计算任务可能会阻塞后续任务的执行，造成饥饿，分布式共享集群还是要用Fair或Capacity。使用FairScheduler自定义队列分为5步：

* 配置yarn-site.xml
* 配置fair-scheduler.xml
* 拷贝修改后的配置文件到集群各节点
* 重启yarn集群
* 在代码中修改任务提交的队列

