package cn.roy.springcloud.mq;

import java.util.HashSet;
import java.util.Set;

/**
 * @Description: 集合测试
 * @Author: Roy Z
 * @Date: 2019-09-13 09:55
 * @Version: v1.0
 */
public class CollectionTest {

    public static void main(String[] args) {
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
