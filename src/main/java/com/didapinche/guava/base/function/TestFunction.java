package com.didapinche.guava.base.function;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import junit.framework.AssertionFailedError;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by Administrator on 2016/7/2.
 */
@RunWith(JUnit4.class)
public class TestFunction {

    List<String> list;
    List list1;
    List list2;
    MergeFunction merge;
    SortFunction sort;
    PrintFunction print;

    @Before
    public void init(){
        list = Lists.newArrayList("1", "2", "3");
        list1 = Lists.newArrayList(5, 8, 7);
        list2 = Lists.newArrayList(3, 9, 1);
        print = PrintFunction.INSTANCE;
        sort = SortFunction.INSTANCE;
        merge = MergeFunction.INSTANCE;
    }

    @Test
    // 对一个list进行排序，并将排序前后进行输出对比
    public void testFunction(){
        // 命令式编程
        List newList = Lists.newArrayList(list1);
        // merge
        newList.addAll(list2);
        // sort
        Collections.sort(newList);
        // print
        Assert.assertEquals("[1, 3, 5, 7, 8, 9]", newList.toString());

        List[] lists = new List[2];
        lists[0] = list1;
        lists[1] = list2;
        // 函数式编程
        Assert.assertEquals("[5, 8, 7]", print.apply(list1));
        Assert.assertEquals("[3, 9, 1]", print.apply(list2));
        // print(sort(merge()))
        Assert.assertEquals("[1, 3, 5, 7, 8, 9]", print.apply(sort.apply(merge.apply(lists))));


        Assert.assertTrue(Predicates.equalTo("[1, 3, 5, 7, 8, 9]").apply(print.apply(sort.apply(merge.apply(lists)))));
    }

    @Test
    public void testCompose(){
        List[] lists = new List[2];
        lists[0] = list1;
        lists[1] = list2;
        // print(sort(merge()))
        Assert.assertEquals("[1, 3, 5, 7, 8, 9]", Functions.compose(print, Functions.compose(sort, merge)).apply(lists));
    }

    @Test
    public void testForMap() {
        Map<String, Integer> map = Maps.newHashMap();
        map.put("One", 1);
        map.put("Three", 3);
        map.put("Null", null);
        Function<String, Integer> function = Functions.forMap(map);

        assertEquals(1, function.apply("One").intValue());
        assertEquals(3, function.apply("Three").intValue());
        assertNull(function.apply("Null"));

        try {
            function.apply("Two");
            throw new AssertionFailedError();
        } catch (IllegalArgumentException expected) {
        }

        Function<String, Integer> function2 = Functions.forMap(map, null);
        assertEquals(1, function2.apply("One").intValue());
        assertEquals(3, function2.apply("Three").intValue());
        assertNull(function2.apply("Null"));
        assertNull(function2.apply("Two"));
    }

}
