# Guava学习笔记
<p style="font-size:32px">&nbsp;&nbsp;&nbsp;&nbsp;——package com.google.common.base</p>

1.函数式编程相关
----------------------------------------
"函数式编程"是一种"编程范式"，主要思想是把运算过程尽量写成一系列嵌套的函数调用。<br/>
特点：
1. 函数是"第一等公民"<br/>
指的是函数与其他数据类型一样，可以作为参数传递，也可以作为返回值<br/>
2. 只用"表达式"，不用"语句"<br/>
"表达式"（expression）是一个单纯的运算过程，总是有返回值；"语句"（statement）是执行某种操作，没有返回值。<br/>
3. 没有"副作用"<br/>
函数要保持独立，所有功能就是返回一个新的值，没有其他行为，尤其是不得修改外部变量的值。<br/>
4. 不修改状态<br/>
不修改变量，意味着状态不能保存在变量中。函数式编程使用参数保存状态，最好的例子就是递归。<br/>
5. 引用透明<br/>
任何时候只要参数相同，引用函数所得到的返回值总是相同的。<br/>

JDK8之前并不支持函数式编程，所以Guava提供了一套函数式编程的工具，包括：
* Predicate&lt;T&gt;  断言接口([Facade模式]())<br/>
![Aaron Swartz](https://raw.githubusercontent.com/leiy88/Guava/master/src/main/resources/Predicate.png)
* Function&lt;F, T&gt;   函数接口<br/>
![Aaron Swartz](https://raw.githubusercontent.com/leiy88/Guava/master/src/main/resources/Function.png)
* Supplier<br/>
![Aaron Swartz](https://raw.githubusercontent.com/leiy88/Guava/master/src/main/resources/Supplier.png)
* Functions  函数工具类，有一些函数实现(结尾带s的都是[工厂模式]())<br/>
![Aaron Swartz](https://raw.githubusercontent.com/leiy88/Guava/master/src/main/resources/Functions.png)<br/>
![Aaron Swartz](https://raw.githubusercontent.com/leiy88/Guava/master/src/main/resources/FunctionsInnerClasses.png)<br/>
-forMap(Map):返回一个搜索Map的函数,apply(key),map中存在key则返回value，否则抛出异常<br/>
-forMap(Map, V):带默认值的搜索Map函数，key不存在是返回默认值，可以是null<br/>
-compose(Function a, Function b):将两个函数进行组合，相当于a.apply(b.apply)([桥接模式]())<br/>
-constant(E):不管输入是什么，都会返回一个常量<br/>
-forPredicate(Predicate):将一个断言转换成返回值为Boolean的函数([适配器模式]())<br/>
-forSupplier(Supplier):将一个Supplier转换成函数，不管传入参数是什么，都返回supplier.get()<br/>
* Predicates 断言工具类，有一些断言的实现<br/>
![Aaron Swartz](https://raw.githubusercontent.com/leiy88/Guava/master/src/main/resources/Predicates.png)<br/>
-compose(Predicate p, Function f):p.apply(f.apply())
* Suppliers<br/>
![Aaron Swartz](https://raw.githubusercontent.com/leiy88/Guava/master/src/main/resources/Suppliers.png)<br/>
-compose(Function f, Supplier s):f.apply(s.get())
-memoize(Supplier):带缓存的Supplier

疑问:Function和Predicate中为什么要定义equals()？

2.字符串工具类
----------------------------------------
* Strings<br/>
Strings 提供了空指针、空字符串的判断和互换方法。<br/>
```
Strings.isNullOrEmpty("");//true<br/>
Strings.nullToEmpty(null);//""<br/>
Strings.nullToEmpty("a");//"a"<br/>
Strings.emptyToNull("");//null<br/>
Strings.emptyToNull("a");//"a"<br/>
```
拿到字符串入参之后，调用一下 nullToEmpty 将可能的空指针变成空字符串，然后也就不用担心字符串引发的 NPE，或者字符串拼接时候出现的 “null” 了。<br/>
<br/>
Strings 还提供了常见的字符串前后拼接同一个字符直到达到某个长度，或者重复拼接自身 n 次。<br/>
```
Strings.padStart("7", 3, '0');//"007"<br/>
Strings.padStart("2016", 3, '0');//"2016"<br/>
Strings.padEnd("4.", 5, '0');//"4.000"<br/>
Strings.padEnd("2016", 3, '!');//"2016"<br/>
Strings.repeat("hey", 3);//"heyheyhey"<br/>
```
#### **源码分析**
```
public static String repeat(String string, int count) {
  checkNotNull(string);  // eager for GWT.
  if (count <= 1) {
    checkArgument(count >= 0, "invalid count: %s", count);
    return (count == 0) ? "" : string;
  }
  // IF YOU MODIFY THE CODE HERE, you must update StringsRepeatBenchmark
  final int len = string.length();
  final long longSize = (long) len * (long) count;
  final int size = (int) longSize;
  if (size != longSize) {
    throw new ArrayIndexOutOfBoundsException("Required array size too large: " + longSize);
  }
  final char[] array = new char[size];
  string.getChars(0, len, array, 0);
  int n;
  for (n = len; n < size - n; n <<= 1) {
    System.arraycopy(array, 0, array, n, n);
  }
  System.arraycopy(array, 0, array, n, size - n);
  return new String(array);
}
int 升级 long 然后降级 int，是为了确保字符串 repeat 之后没有超过 String 的长度限制，而先强制提升然后截断的方法，能够高效的判断溢出
```
<br/>
Strings 的最后一组功能是查找两个字符串的公共前缀、后缀。<br/>
Strings.commonPrefix("aaab", "aac");//"aa"<br/>
Strings.commonSuffix("aaac", "aac");//"aac"<br/>

* CharMatcher<br/>
* CaseFormat<br/>
* Spliter<br/>
* Joiner<br/>

3.实用工具类
----------------------------------------
* PreConditions<br/>
* Verify<br/>
* Defaults<br/>
* Enums<br/>
* Equivalence<br/>
* Objects<br/>
* StopWatch<br/>
* Throwables<br/>