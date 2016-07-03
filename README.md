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
* Predicate&lt;T&gt;  断言接口([Facade模式](https://www.baidu.com/s?wd=Facade模式))<br/>
![Aaron Swartz](https://raw.githubusercontent.com/leiy88/Guava/master/src/main/resources/Predicate.png)
* Function&lt;F, T&gt;   函数接口<br/>
![Aaron Swartz](https://raw.githubusercontent.com/leiy88/Guava/master/src/main/resources/Function.png)
* Supplier<br/>
![Aaron Swartz](https://raw.githubusercontent.com/leiy88/Guava/master/src/main/resources/Supplier.png)
* Functions  函数工具类，通过内部类提供了很多实用函数(结尾带s的都是[工厂模式](https://www.baidu.com/s?wd=工厂模式))<br/>
![Aaron Swartz](https://raw.githubusercontent.com/leiy88/Guava/master/src/main/resources/Functions.png)<br/>
![Aaron Swartz](https://raw.githubusercontent.com/leiy88/Guava/master/src/main/resources/FunctionsInnerClasses.png)<br/>
    * forMap(Map):返回一个搜索Map的函数,apply(key),map中存在key则返回value，否则抛出异常<br/>
    * forMap(Map, V):带默认值的搜索Map函数，key不存在是返回默认值，可以是null<br/>
    * compose(Function a, Function b):将两个函数进行组合，相当于a.apply(b.apply)([桥接模式](https://www.baidu.com/s?wd=桥接模式))<br/>
    * constant(E):不管输入是什么，都会返回一个常量<br/>
    * forPredicate(Predicate):将一个断言转换成返回值为Boolean的函数([适配器模式](https://www.baidu.com/s?wd=适配器模式))<br/>
    * forSupplier(Supplier):将一个Supplier转换成函数，不管传入参数是什么，都返回supplier.get()<br/>
* Predicates 断言工具类，有一些断言的实现<br/>
![Aaron Swartz](https://raw.githubusercontent.com/leiy88/Guava/master/src/main/resources/Predicates.png)<br/>
-compose(Predicate p, Function f):p.apply(f.apply())
* Suppliers<br/>
![Aaron Swartz](https://raw.githubusercontent.com/leiy88/Guava/master/src/main/resources/Suppliers.png)<br/>
    * compose(Function f, Supplier s):f.apply(s.get())
    * memoize(Supplier):带缓存的Supplier

疑问:Function和Predicate中为什么要定义equals()？

2.字符串工具类
----------------------------------------
* Strings<br/>
Strings 提供了空指针、空字符串的判断和互换方法。<br/>
```
Strings.isNullOrEmpty("");//true
Strings.nullToEmpty(null);//""
Strings.nullToEmpty("a");//"a"
Strings.emptyToNull("");//null
Strings.emptyToNull("a");//"a"
```
拿到字符串入参之后，调用一下 nullToEmpty 将可能的空指针变成空字符串，然后也就不用担心字符串引发的 NPE，或者字符串拼接时候出现的 “null” 了。<br/>
<br/>
Strings 还提供了常见的字符串前后拼接同一个字符直到达到某个长度，或者重复拼接自身 n 次。<br/>
```
Strings.padStart("7", 3, '0');//"007"
Strings.padStart("2016", 3, '0');//"2016"
Strings.padEnd("4.", 5, '0');//"4.000"
Strings.padEnd("2016", 3, '!');//"2016"
Strings.repeat("hey", 3);//"heyheyhey"
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
```
int 升级 long 然后降级 int，是为了确保字符串 repeat 之后没有超过 String 的长度限制，<br/>
而先强制提升然后截断的方法，能够高效的判断溢出<br/>
<br/>
Strings 的最后一组功能是查找两个字符串的公共前缀、后缀。<br/>
```
Strings.commonPrefix("aaab", "aac");//"aa"
Strings.commonSuffix("aaac", "aac");//"aac"
```
* CharMatcher<br/>
    CharMatcher提供了多种对字符串处理的方法, 它的主要意图有:<br/>
    1. 找到匹配的字符<br/>
    2. 处理匹配的字符<br/>

    CharMatcher内部主要实现包括两部分:<br/>
    1. 实现了大量公用内部类, 用来方便用户对字符串做匹配: 例如 JAVA_DIGIT 匹配数字, JAVA_LETTER 匹配字母等等.<br/>
    2. 实现了大量处理字符串的方法, 使用特定的CharMatcher可以对匹配到的字符串做出多种处理, 例如 remove(), replace(), trim(), retain()等等<br/>
CharMatcher本身是一个抽象类, 其中一些操作方法是抽象方法, 他主要依靠内部继承CharMatcher的内部子类来实现抽象方法和重写一些操作方法, 因为不同的匹配规则的这些操作方法具有不同的实现要求([模板方法模式](https://www.baidu.com/s?wd=模板方法模式))<br/>
#### 常用操作方法
CharMatcher negate(): 返回以当前Matcher判断规则相反的Matcher
CharMatcher and(CharMatcher other): 返回与other匹配条件组合做与来判断的Matcher
CharMatcher or(CharMatcher other): 返回与other匹配条件组合做或来判断的Matcher
```
CharMatcher.javaUpperCase().matches('A'); //true
CharMatcher.javaUpperCase().negate().matches('A'); //false
CharMatcher.javaUpperCase().matches('a'); //false
CharMatcher.javaUpperCase().negate().matches('a'); //true
CharMatcher.is('c').indexIn("abcdc"); //2
CharMatcher.is('c').indexIn("abcdc", 3); //3
CharMatcher.is('c').and(CharMatcher.noneOf("abd")).matches('c') //true
CharMatcher.is('c').and(CharMatcher.noneOf("abc")).matches('c') //false
```
疑问：setBits()干嘛用的？
附：[BitSet讲解](http://blog.csdn.net/feihong247/article/details/7849317)
* Ascii<br/>
提供了一组操作Ascii(0x00~0x7F)字符的方法，如下：<br/>
![Aaron Swartz](https://raw.githubusercontent.com/leiy88/Guava/master/src/main/resources/Ascii.png)
* CaseFormat<br/>
不同命名方式转换的工具类，不支持Ascii以外的字符<br/>
通过枚举定义了各种格式：<br/>
    * LOWER_HYPHEN 中折线命名，如test-test<br/>
    * LOWER_UNDERSCORE 小写下划线命名，如test_test<br/>
    * UPPER_UNDERSCORE 大写下划线命名，如TEST_TEST<br/>
    * LOWER_CAMEL 小写驼峰命名，如testTest<br/>
    * UPPER_CAMEL 大写驼峰命名，如TestTest<br/>
两个模板方法：<br/>
    * to(CaseFormat, String)<br/>
    * convert(CaseFormat, String)<br/>
两个方法很类似，只有一点不同，如果两种格式相同to不会做转换，直接返回原字符串，而conver依然会做一次转换
```
UPPER_CAMEL.convert(UPPER_CAMEL, "testTest") //TestTest
UPPER_CAMEL.to(UPPER_CAMEL, "testTest") //testTest
UPPER_CAMEL.to(LOWER_UNDERSCORE, "testTest") //test_test
```
### 源码分析<br/>
通过枚举实现单例工厂

* Spliter<br/>
提供各种字符串分割方法<br/>

Spliter与apache commons的StringUtils对比：
    1.Spliter面向对象，StringUtils面向过程
```
// Apache StringUtils...
String[] tokens1= StringUtils.split("one,two,three",',');

// Google Guava splitter...
Iteratable<String> tokens2 = Splitter.on(','),split("one,two,three");
```
    2.Spliter分割结果是迭代器(写快读慢)，StringUtils是数组(读快写慢)
附：[Spliter与apache commons的StringUtils对比](http://vipcowrie.iteye.com/blog/1513693)
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