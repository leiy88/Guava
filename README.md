# Guava学习笔记
<p style="font-size:32px">&nbsp;&nbsp;&nbsp;&nbsp;——package com.google.common.base</p>

1.函数式编程相关
----------------------------------------
"函数式编程"是一种"编程范式"，主要思想是把运算过程尽量写成一系列嵌套的函数调用。
JDK8之前并不支持函数式编程，所以Guava提供了一套函数式编程的工具，包括：
* Predicate&lt;T&gt;  断言接口<br/>
![Aaron Swartz](https://raw.githubusercontent.com/leiy88/Guava/master/src/main/resources/Predicate.png)
* Function&lt;F, T&gt;   函数接口<br/>
![Aaron Swartz](https://raw.githubusercontent.com/leiy88/Guava/master/src/main/resources/Function.png)
* Supplier<br/>
![Aaron Swartz](https://raw.githubusercontent.com/leiy88/Guava/master/src/main/resources/Supplier.png)
* Predicates 断言工具类，有一些断言的实现<br/>
![Aaron Swartz](https://raw.githubusercontent.com/leiy88/Guava/master/src/main/resources/Predicates.png)<br/>
![Aaron Swartz](https://raw.githubusercontent.com/leiy88/Guava/master/src/main/resources/PredicatesInnerClasses.png)
* Functions  函数工具类，有一些函数实现<br/>
![Aaron Swartz](https://raw.githubusercontent.com/leiy88/Guava/master/src/main/resources/Functions.png)<br/>
* Suppliers<br/>
![Aaron Swartz](https://raw.githubusercontent.com/leiy88/Guava/master/src/main/resources/Suppliers.png)<br/>

疑问:Function和Predicate中为什么要定义equals()？

2.字符串工具类
----------------------------------------
* Spliter
* Joiner

3.其他
----------------------------------------