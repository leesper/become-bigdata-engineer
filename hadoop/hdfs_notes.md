# HDFS分布式文件系统学习笔记

## 知识点

* Hadoop项目起源
* 版本发展历史
* Hadoop架构模块
* HDFS模块重要组件
* 分块存储、副本策略与机架感知
* 安全模式
* HDFS Shell与Java API操作
* 数据检查点冷备份原理

Hadoop项目起源于Nutch，后者的设计目标是大型的全网搜索引擎。在开发的过程中很快凸显了2个问题，一个是如何对海量文件进行存储；另一个是如何对这么多文件计算索引。谷歌发表了两篇论文，一篇介绍分布式文件系统，一篇介绍分布式计算的MR模型，提供了解决方案，Hadoop的基础就是这两者的开源实现。

Hadoop从0.x、1.x、2.x到现在的3.x，已经发展了多个版本。主要的架构模块有三个：HDFS、YARN和MapReduce。HDFS模块包含三个重要组件：

1. NameNode：负责维护文件系统元数据信息
2. DataNode：以数据块形式存储的数据
3. SecondaryNameNode：协助NameNode进行元数据管理，特别是数据冷备份

Yarn包含两个重要组件：

1. ResourceManager：负责资源的分配和调度工作
2. NodeManager：负责具体的分布式计算任务

HDFS是采用分块存储方式来存储文件的，比如300M的文件，会分成128M、128M和44M三个block块进行存储，每个block块默认有3个冗余备份，称为“三副本存储”，这是可以通过配置进行修改的。副本的存储策略是机架感知的，也就是说HDFS文件系统会将数据块分散到不同机架上进行冗余存储，提高数据容错性。抽象成数据块既简化了存储子系统的设计，又方便数据的冗余备份。一个文件可能比单个DataNode节点的容量都大，但通过抽象成数据块的方式进行存储，则提供了存储大文件的可能性。

|          |           NameNode            |          DataNode          |
| -------- | :---------------------------: | :------------------------: |
| 存什么？ |        文件系统元数据         |          文件内容          |
| 存哪里？ |             内存              |            磁盘            |
| 具体数据 | 文件、block，DataNode之间映射 | block id和本地文件映射关系 |

安全模式是HDFS所处的一种特殊状态，一般发生在整个系统启动的时候。在安全模式下NameNode会从磁盘上读取fsimage文件和edits.log文件，加载到内存中进行合并，还原出分布式文件系统的元数据信息，此时文件系统只处理读请求，任何写操作都会失败。NameNode不会在fsimage中持久化block在系统中的具体位置，这个信息是DataNode负责维护的，因此它启动后需要等待DataNode重新上报这个信息，才能正常工作，当NameNode收集到99.9%的数据块的最小副本条件并等待30s后，就会退出安全模式。

HDFS Shell命令与Linux Shell命令类似，可以举一反三地使用，要学会使用help查看帮助信息。HDFS的文件系统主要是用来存储文件，它虽然也提供类似Linux文件系统那样的访问权限管理，但并不会做太多限制。HDFS还可以通过Java API进行访问，其基本操作套路是：

* 创建Configuration对象，进行必要信息的配置
* 获取FileSystem对象
* 通过FileSystem对象进行各种操作

SecondaryNameNode的主要作用是协助NameNode进行数据的冷备份。每次HDFS文件系统收到写请求时，总是会先写edits.log，写成功后再更新内存中的文件系统元数据，日志文件用于保证事务，元数据用于响应后续的读文件操作。如果一直不停地写，edits.log就会变得非常大，系统重启后要从日志恢复出元数据耗时也比较长，所以 edits.log事实上是由多个文件分片组成的，且任一时间内只有一个文件分片正在写入日志，那么这么多文件分片是怎么形成的呢？这就要通过SecondaryNameNode的冷备份来实现了。

fsimage是存储在磁盘上的数据持久化检查点文件，用于在NN故障时结合edits.log对文件系统元数据进行快速恢复。触发冷备份数据持久化检查要满足下列任一条件：

1. 1个小时时间间隔
2. 日志分片文件已写入100w事务

此时SNN就会向NN发送请求，NN会回滚生成新的日志分片文件，后续的写操作都会向其中写入日志。SNN获取fsimage文件和edits.log文件，加载到自己的内存中，然后进行合并，生成新的fsimage文件，随后该文件会返回给NN，替换掉原来的文件，从而完成fsimage的更新。

## 习题解答

### 1. 本地开发环境配置

我使用的是Mac Book笔记本，采用的是MacOS操作系统，它本质上是一个Unix系统，故不需要进行配置。只需要安装Java、Intellij IDEA和maven就可以使用了。

### 2. 编程实现创建hdfs的/kkb/目录，并上传文件a.txt到此目录

```java
Configuration conf = new Configuration();
conf.set("fs.defaultFS", "hdfs://node01:8020");
FileSystem fs = FileSystem.get(conf);
Path src = new Path("file:///Users/likejun/become-bigdata-engineer/hadoop/hdfs/a.txt");
Path dst = new Path("/kkb/a.txt");
fs.copyFromLocalFile(src, dst);
fs.close();
```

运行结果截图：

![](./assets/result.png)