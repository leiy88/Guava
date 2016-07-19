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
In distributed application engineering, the word node can refer to a generic host machine, a server, a member of an ensemble, a client process, etc. In the ZooKeeper documentation, znodes refer to the data nodes. Servers refer to machines that make up the ZooKeeper service; quorum peers refer to the servers that make up an ensemble; client refers to any host or process which uses a ZooKeeper service.
```
