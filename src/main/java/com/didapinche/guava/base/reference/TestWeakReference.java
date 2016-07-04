package com.didapinche.guava.base.reference;

/**
 * Created by Sean on 16/07/04.
 */
public class TestWeakReference {

    public static void main(String[] args) {

        // a是强引用
        A a = new A();
        B b = new B(a);
        a = null;
        System.gc();
        System.out.println(b.getA() == null);


    }

    public static class VeryBig{
        public String name;
        public byte[] b = new byte[2048];
    }

    public static class A{
        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            System.out.println("A.finalize");
        }
    }

    public static class B{
        private A a;

        public B(A a) {
            this.a = a;
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            System.out.println("B.finalize");
        }

        public A getA() {
            return a;
        }

        public void setA(A a) {
            this.a = a;
        }
    }
}
