# HBase实战

## 知识点

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

4. 用画图加文字的方式，描述HBase写入数据的流程（手画或画图软件均可），并提供截图
5. 编程题

hbase第一次课时，编程已经创建了表myuser；现在使用mr编程，将此表的数据读出来，写入一个HDFS文件/user/hadoop/myuser.txt

提示：hbase第三次课讲了mr读hbase表，结果写入另外一个hbase表；也讲了mr读取hdfs文件，结果写入一个HBase表。本作业需要实现mr读hbase表；然后结果写入HDFS