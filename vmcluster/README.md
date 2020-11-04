# 搭建虚拟机集群

## 知识点

* 使用Vagrant配置三台Linux虚拟机，采用CentOS7
* 对虚拟机环境进行基础配置
  * 关闭防火墙
  * 关闭selinux
  * 更改主机名
  * 建立主机名和IP地址映射
  * 配置时钟同步
  * 添加hadoop用户账户
  * 定义统一目录
  * 配置SSH免密登录
  * 安装JDK
* 安装配置Hadoop集群
  * 上传压缩包并解压
  * 修改配置文件
  * 创建文件存放目录
  * 分发安装包到各虚拟机
  * 配置hadoop环境变量
  * 格式化集群
  * 启动集群
  * 验证集群是否搭建成功
* 安装配置ZooKeeper集群
  * 上传压缩包并解压
  * 修改配置文件
  * 分发安装包到各虚拟机
  * 添加myid配置
  * 配置环境变量
  * 三台机器启动zookeeper服务

## 启动3台Linux虚拟机

利用Vagrant配置三台CentOS7的Linux虚拟机，以私有网络的方式组网，IP地址分别为：

* 192.168.51.100
* 192.168.51.110
* 192.168.51.120

使用Vagrant启动虚拟机可以省去繁琐的操作系统安装和克隆虚拟机的操作，只需要编写好Vagrantfile配置文件就可以`vagrant up`一键启动虚拟机。

```ruby
# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure("2") do |config|
  # https://docs.vagrantup.com.
  config.vm.define "node01" do |node01|
    node01.vm.box = "centos/7"
    node01.vm.network "private_network", ip: "192.168.51.100"
  end

  config.vm.define "node02" do |node02|
    node02.vm.box = "centos/7"
    node02.vm.network "private_network", ip: "192.168.51.110"
  end

  config.vm.define "node03" do |node03|
    node03.vm.box = "centos/7"
    node03.vm.network "private_network", ip: "192.168.51.120"
  end
end
```

