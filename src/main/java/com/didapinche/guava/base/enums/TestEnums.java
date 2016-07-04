package com.didapinche.guava.base.enums;

import com.google.common.base.Enums;

/**
 * Created by Administrator on 2016/7/3.
 */
public class TestEnums {
    private enum Color{
        RED("red"), GREEN("green"), BLUE("blue");
        private String name;

        Color(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }


        @Override
        public String toString() {
            return "name:" + this.getName();
        }
    }

    public static void main(String[] args) throws IllegalAccessException, NoSuchFieldException {
        System.out.println(Color.RED.toString());
        System.out.println(Enums.getField(Color.RED).get(null));
        System.out.println(Enums.getIfPresent(Color.class, "REdD").isPresent());
        System.out.println(Enums.valueOfFunction(Color.class).apply("RED"));
        System.out.println(Enum.valueOf(Color.class, "RED"));
    }
}
