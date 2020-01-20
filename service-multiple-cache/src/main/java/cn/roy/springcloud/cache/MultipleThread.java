package cn.roy.springcloud.cache;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019-12-13 15:11
 * @Version: v1.0
 */
public class MultipleThread {

    public static void main(String[] args) {
//        Product product = new Product();
//        Producer producer = new Producer(product);
//        Consumer consumer = new Consumer(product);
//        new Thread(producer).start();
//        new Thread(consumer).start();

        final BoundedBuffer boundedBuffer = new BoundedBuffer();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("t1 run");
                for (int i = 0; i < 1000; i++) {
                    try {
                        System.out.println("putting..");
                        boundedBuffer.put(Integer.valueOf(i));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 1000; i++) {
                    try {
                        Object val = boundedBuffer.take();
                        System.out.println(val);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t1.start();
        t2.start();
    }

    public static class Product {
        private List<Integer> data = new ArrayList<>();
        private int no = 0;

        public void add() {
            if (data.size() > 3) {
                try {
                    synchronized (data) {
                        data.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            no++;
            synchronized (data) {
                data.add(no);
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            System.out.println(simpleDateFormat.format(new Date()) + "，生成者生产了一个产品，产品编号：" + no);
            synchronized (data) {
                data.notifyAll();
            }
        }

        public Integer del() {
            if (data.size() == 0) {
                try {
                    synchronized (data) {
                        data.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Integer remove = data.remove(0);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            System.out.println(simpleDateFormat.format(new Date()) + "，消费者消费了一个产品，产品编号：" + remove);
            synchronized (data) {
                data.notifyAll();
            }
            return remove;
        }

    }

    public static class Producer implements Runnable {
        private Product product;

        public Producer(Product product) {
            this.product = product;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(3000);
                    product.add();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static class Consumer implements Runnable {
        private Product product;

        public Consumer(Product product) {
            this.product = product;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    product.del();
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class BoundedBuffer {
        final Lock lock = new ReentrantLock();
        final Condition notFull = lock.newCondition();
        final Condition notEmpty = lock.newCondition();

        final Object[] items = new Object[100];
        int putptr, takeptr, count;

        public void put(Object x) throws InterruptedException {
            System.out.println("put wait lock");
            lock.lock();
            System.out.println("put get lock");
            try {
                while (count == items.length) {
                    System.out.println("buffer full, please wait");
                    notFull.await();
                }

                items[putptr] = x;
                if (++putptr == items.length)
                    putptr = 0;
                ++count;
                notEmpty.signal();
            } finally {
                lock.unlock();
            }
        }

        public Object take() throws InterruptedException {
            System.out.println("take wait lock");
            lock.lock();
            System.out.println("take get lock");
            try {
                while (count == 0) {
                    System.out.println("no elements, please wait");
                    notEmpty.await();
                }
                Object x = items[takeptr];
                if (++takeptr == items.length)
                    takeptr = 0;
                --count;
                notFull.signal();
                return x;
            } finally {
                lock.unlock();
            }
        }
    }

}
