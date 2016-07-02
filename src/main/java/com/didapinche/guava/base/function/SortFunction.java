package com.didapinche.guava.base.function;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2016/7/2.
 */
public enum SortFunction implements Function<List, List> {
    INSTANCE;

    @Override
    public List apply(List list) {
        System.out.println("Sort...");
        List newList = Lists.newArrayList(list);
        Collections.sort(newList);
        return newList;
    }
}
