package com.didapinche.guava.base.function;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by Administrator on 2016/7/2.
 */
public enum  MergeFunction implements Function<List[], List> {
    INSTANCE;

    @Override
    public List apply(List[] lists) {
        Preconditions.checkNotNull(lists);
        System.out.println("Merge...");
        List merge = Lists.newArrayList();
        for(List list : lists){
            merge.addAll(list);
        }
        return merge;
    }
}
