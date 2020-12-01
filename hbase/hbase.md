# HBase实战

## 知识点

* HBase的安装部署
  * 在已启动zk和hadoop的集群上搭建hbase
* HBase的核心概念
  * **高可靠性**、**高性能**、**列存储**、**可伸缩**、**实时读写**的分布式数据库系统

* HBase的特点
  * 海量存储、列式、易扩展、高并发读写、稀疏数据友好、数据多版本和数据类型单一（字节数组）

* HBase的架构图
  * HMaster、HRegonServer以及ZK各自发挥的作用

* HBase存储数据结构
  * 行键、列族、列和单元格

* HBase shell命令基本操作

* HBase Java API 的基本读写
* HBase的数据存储原理
  * region、store、memstore和storefile的关系
  * region和表的对应关系、store和列族对应关系
* HBase的读写流程
* HBase表的region拆分和合并
* HBase表的预分区

## 思考题

1. 简答：HBase表的数据模型有哪些？每个给出简要说明

答：HBase表的数据模型有行键（rowkey）、列族（column family）、列（column）和单元格（cell）。行键是行的标识，每一行都有一个rowkey，相同rowkey对应的都是一行的数据；列族是表的schema的一部分，创建表时必须指定列族；任何一个列都必须属于某个列族，同一列族的列是存储在一起的；行键、列族和列确定一个单元格，单元格中以字节码形式存储数据。

2. 使用hbase命令行客户端，进行表的操作

（1）创建一个表，表名 "firsttable" ，包含两个列族，分别是col1 col2，其中col1设置可保留版本数为3，col2为1个

答：建表命令：`create 'firsttable', {NAME => 'col1', VERSIONS => '3'}, {NAME => 'col2', VERSIONS => 1}`

（2）向表中添加数据，要求：

行键是rk001；向col1添加一列，列名为"oid", value是"order071701"；向col2添加一列，列名为"proid"，value是"pid00ab"；再向col1的列"oid"写入新的值，value是"order071702"

答：添加数据命令：

* `put 'firsttable', 'rk001', 'col1:oid', 'order071701'`
* `put 'firsttable', 'rk001', 'col2:proid', 'pid00ab'`
* `put 'firsttable', 'rk001', 'col1:oid', 'order071702'`

（3）查询firsttable表中，行键为rk001的行中的，col1列族下oid列的最新的两个版本的值

答：查询命令：`get 'firsttable', 'rk001', {COLUMNS => 'col1:oid', RAW => true, VERSIONS => 2}`

3. 简答：为什么要对HBase表进行预分区？

答：HBase默认为每个创建的表都分配了一个region，此后所有的写操作都会写入同一个region，在同一个HRegionServer上，这不利于负载均衡和灾备。可以在创建表的时候预分区来解决这个问题。

4. 用画图加文字的方式，描述HBase写入数据的流程（手画或画图软件均可），并提供截图

答：写流程如下

* 客户端先查询ZooKeeper获取meta表所在的region及其HRegionServer
* 客户端访问对应的HRegionServer，根据命名空间、表名和rowkey从meta表中获取要写入数据对应的region所在的HRegionServer
* 客户端再次访问HRegionServer，定位到对应的region，进行写入，数据同时写入HLog和memstore中
* memstore到达阈值后刷写到磁盘，生成storefile

4. 编程题

使用MR编程，将firsttable表的数据读出来，写入一个HDFS文件/user/hadoop/firsttable.txt

提示：hbase第三次课讲了mr读hbase表，结果写入另外一个hbase表；也讲了mr读取hdfs文件，结果写入一个HBase表。本作业需要实现mr读hbase表；然后结果写入HDFS