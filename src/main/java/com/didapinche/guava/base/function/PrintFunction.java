package com.didapinche.guava.base.function;

import com.google.common.base.Function;
import com.google.common.base.Functions;

import java.util.List;

/**
 * Created by Administrator on 2016/7/2.
 */
public enum  PrintFunction implements Function<List, String> {
    INSTANCE;

    @Override
    public String apply(List list) {
       return Functions.toStringFunction().apply(list);
    }
}
