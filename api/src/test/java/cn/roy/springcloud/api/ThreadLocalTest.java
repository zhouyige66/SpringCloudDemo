package cn.roy.springcloud.api;

import java.util.HashSet;
import java.util.Set;

/**
 * @Description: bb
 * @Author: Roy Z
 * @Date: 2019-05-13 09:46
 * @Version: v1.0
 */
public class ThreadLocalTest {
    static class ResourceClass {
        public final static ThreadLocal<String> RESOURCE_1 =
                new ThreadLocal<String>();

        public final static ThreadLocal<String> RESOURCE_2 =
                new ThreadLocal<String>();
    }

    static class A {

        public void setOne(String value) {
            ResourceClass.RESOURCE_1.set(value);
        }

        public void setTwo(String value) {
            ResourceClass.RESOURCE_2.set(value);
        }
    }

    static class B {
        public void display() {
            System.out.println(ResourceClass.RESOURCE_1.get()
                    + ":" + ResourceClass.RESOURCE_2.get());
        }
    }

    public static void main(String []args) {
//        final A a = new A();
//        final B b = new B();
//        for(int i = 0 ; i < 15 ; i ++) {
//            final String resouce1 = "线程-" + i;
//            final String resouce2 = " value = (" + i + ")";
//            new Thread() {
//                public void run() {
//                    try {
//                        a.setOne(resouce1);
//                        a.setTwo(resouce2);
//                        b.display();
//                    }finally {
//                        ResourceClass.RESOURCE_1.remove();
//                        ResourceClass.RESOURCE_2.remove();
//                    }
//                }
//            }.start();
//        }
        Set<Long> set1 = new HashSet<>();
        set1.add(1L);
        set1.add(2L);
        set1.add(3L);
        set1.add(4L);
        Set<Long> set2 = new HashSet<>();
        set2.add(1L);
        set2.add(2L);
        set2.add(5L);
        set1.removeAll(set2);
        System.out.println(set1);
    }
}
