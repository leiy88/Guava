简介
==================
本文档可以给分布式系统及想利用zk进行服务协同管理的开发者提供指引。包含了一些概念性的描述和实用信息。<br>
本指南最开始的四个部分从比较高的层面对zk的概念进行了论述，这对理解zk原理及熟练实用是非常必要的。本指南不包含源码，读者需要对分布式计算有一定的了解。本节内容包括：<br>
* [The ZooKeeper Data Model]()<br>
* [ZooKeeper Sessions]()<br>
* [ZooKeeper Watches]()<br>
* [Consistency Guarantees]()<br>

第二节提供了一些编程方面的实用信息，包括：<br>
* [Building Blocks: A Guide to ZooKeeper Operations]()<br>
* [Bindings]()<br>
* [Program Structure, with Simple Example [tbd]]()<br>
* [Gotchas: Common Problems and Troubleshooting]()<br>

本文附录包含了一些其他与zk相关的实用信息。<br>
本文大部分内容可以独立阅读。但是，在编写你的第一个zk应用之前，你至少需要阅读 ZooKeeper Data Model 和 ZooKeeper Basic Operations这两个章节。并且我们提供了一个demo来帮助你更快的理解zk应用的基本架构。<br>

The ZooKeeper Data Model
==================
zk有一个类似于分布式文件系统层级机构的命名空间。唯一的不同是每一个节点都可以存放数据并且可以拥有子节点。就好比一个即是文件又是目录的文件系统。每一个节点都表现为 / 分隔的绝对路径；并不支持相对路径。路径中可以使用所有unicode字符，但是有如下限制：<br>
* 空字符(\u0000)不能用于路径名(这会导致C语言绑定问题)。<br>
* \u0001 - \u0019 and \u007F - \u009F 这些字符会导致显示问题，所以不应该在路径中使用。<br>
* 如下字符不允许在路径中使用：\ud800 -uF8FFF, \uFFF0-uFFFF, \uXFFFE - \uXFFFF (X代表十六进制的1-E), \uF0000 - \uFFFFF.<br>
* "." 可以用作路径的一部分，但不能单独用 "." 或 ".."，因为zk不能使用相对路径。比如这些路径是不合法的："/a/b/./c" or "/a/b/../c".<br>
* "zookeeper" 是一个关键词，不能被使用，zk预建了zookeeper节点。<br>

ZNodes
==================
zk树状结构的每个节点被称之为 znode 。znode包含一个描述其状态的数据结构，包括数据和 ACL 变更的版本号，以及时间戳。版本号和时间戳一起就可以让zk验证缓存并协调更新。每当znode的数据有变化，版本号都会加1。例如，每次客户端取数据的时候版本号都会一同被拿到。当客户端执行更新或删除的时候必须提供数据版本号。如果提供的版本号与实际的数据版本号不一致，更新会失败。(这个动作可以被重写，参见...[tbd...])<br>

```
在分布式应用中，一个字符节点可以代表主机、服务器、集群的成员或者一个客户端进程等。在zk文档中，znode代表一个数据节点。
servers 是指组成ZooKeeper服务的机器;quorum peers指的是构成一个集群的服务器;client指的是使用zk的任何一台主机或进程。
```
开发者最常使用的就是 znode 。通常扮演如下几种角色：<br>

Watches
-----------------
客户端会监控 znode 。节点的改变会出发监控并清除当前监控。当一个监控被触发，zk会通知客户端。更详细的描述在 ZooKeeper Watches 章节。

Data Access
-----------------
每一个节点存储的数据都可以被原子性的读写。读操作会读取znode下所有的数据，反过来，写操作会替换所有数据。每个节点都通过访问控制列表来进行权限限制。<br>

zk设计之初就不是用来存储大型数据的, 它是用来进行数据协调的。这些数据的表现形式一般是配置信息、状态信息或者 rendezvous 等。他们的共同点是数据量相对较小，以KB为单位。zk客户端和服务端都会进行一些 check 来保证znode的数据量在1M以下，但平均来说，远达不到这个数据量。数据量较大的话，由于通过网络传输数据到存储介质会花费额外的时间，所以影响有些操作的延时。如果必须要存贮大型数据，一般采取的方式是将数据存放在大容量存储系统上例如 NFS 或 HDFS，然后将位点信息存放在zk上。

Ephemeral Nodes
-----------------
zk还有临时节点的概念。临时节点就是当创建它的会话不活动的时候就会被删除。由于这个特性，临时节点不可以有子节点。

Sequence Nodes -- Unique Naming
-----------------
在创建节点的时候你可以让zk在节点路径的末尾附加一个单调递增的计数器。这个计数器相对于父节点来说是唯一的。计数器的格式是 %010d，即左侧补0的十位数字(用来简化排序)。例如："<path>0000000001" 。参照队列说明的例子来使用此特性。PS：父节点会为第一级子节点维护一个有符号整型(4bytes)计数器，当计数器超过2147483647时会溢出(最终的节点名会是"<path>-2147483647")。

Time in ZooKeeper
=================
zk跟踪时间有多种方式：

* Zxid
每次zk状态变更都会受到一个 zxid(ZooKeeper Transaction Id)形式的时间戳。它能表现出 zk 变更的顺序。每次变更都有一个唯一的 zxid，如果 zxid1 比 zxid2 小那么就能知道 zxid1发生在 zxid2 之前。
* Version numbers
Every change to a node will cause an increase to one of the version numbers of that node. The three version numbers are version (number of changes to the data of a znode), cversion (number of changes to the children of a znode), and aversion (number of changes to the ACL of a znode).
* Ticks
When using multi-server ZooKeeper, servers use ticks to define timing of events such as status uploads, session timeouts, connection timeouts between peers, etc. The tick time is only indirectly exposed through the minimum session timeout (2 times the tick time); if a client requests a session timeout less than the minimum session timeout, the server will tell the client that the session timeout is actually the minimum session timeout.
* Real time
ZooKeeper doesn't use real time, or clock time, at all except to put timestamps into the stat structure on znode creation and znode modification.