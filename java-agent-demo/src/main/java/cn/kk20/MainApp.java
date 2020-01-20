package cn.kk20;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2020-01-09 13:38
 * @Version: v1.0
 */
@SpringBootApplication
@EnableSwagger2
public class MainApp {

    public static void main(String[] args) {
        SpringApplication.run(MainApp.class, args);

        // 线程池
        // 几种通用的线程池
        /**
         * 创建一个单工作线程执行的线程池，返回的实例不可再重新配置
         * 构建是包装ThreadPoolExecutor，队列使用LinkedBlockingQueue
         */
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        /**
         * 创建一个重用固定数量线程的线程池，操作一个共享的无限队列。
         * 线程存储队列使用LinkedBlockingQueue
         */
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
        /**
         * 创建根据需要创建新线程的线程池，可复用先前创建的线程（如果可用），无可复用线程，则新建线程然后加入线程池
         * 几个重要参数：0个核心线程，最大线程数Integer.MAX_VALUE，线程空闲60S则被移出线程池
         * 队列使用SynchronousQueue，实际上它不是一个真正的队列，因为它不会为队列中元素维护存储空间。与其他队列不同的是，它维护
         * 一组线程，这些线程在等待着把元素加入或移出队列。在使用SynchronousQueue作为工作队列的前提下，客户端代码向线程池提交任
         * 务时，而线程池中又没有空闲的线程能够从SynchronousQueue队列实例中取一个任务，那么相应的offer方法调用就会失败（即任务
         * 没有被存入工作队列）。此时，ThreadPoolExecutor会新建一个新的工作者线程用于对这个入队列失败的任务进行处理（假设此时
         * 线程池的大小还未达到其最大线程池大小maximumPoolSize）
         */
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        /**
         * 创建并行运行线程池（处理器核心数）
         */
        ExecutorService workStealingPool = Executors.newWorkStealingPool();
        /**
         * 可定期运行的线程池
         */
        ScheduledExecutorService singleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);



        Executors.unconfigurableExecutorService(new ExecutorService() {
            @Override
            public void shutdown() {

            }

            @Override
            public List<Runnable> shutdownNow() {
                return null;
            }

            @Override
            public boolean isShutdown() {
                return false;
            }

            @Override
            public boolean isTerminated() {
                return false;
            }

            @Override
            public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
                return false;
            }

            @Override
            public <T> Future<T> submit(Callable<T> task) {
                return null;
            }

            @Override
            public <T> Future<T> submit(Runnable task, T result) {
                return null;
            }

            @Override
            public Future<?> submit(Runnable task) {
                return null;
            }

            @Override
            public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
                return null;
            }

            @Override
            public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
                return null;
            }

            @Override
            public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
                return null;
            }

            @Override
            public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return null;
            }

            @Override
            public void execute(Runnable command) {

            }
        });
        Executors.unconfigurableScheduledExecutorService(new ScheduledExecutorService() {
            @Override
            public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
                return null;
            }

            @Override
            public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
                return null;
            }

            @Override
            public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
                return null;
            }

            @Override
            public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
                return null;
            }

            @Override
            public void shutdown() {

            }

            @Override
            public List<Runnable> shutdownNow() {
                return null;
            }

            @Override
            public boolean isShutdown() {
                return false;
            }

            @Override
            public boolean isTerminated() {
                return false;
            }

            @Override
            public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
                return false;
            }

            @Override
            public <T> Future<T> submit(Callable<T> task) {
                return null;
            }

            @Override
            public <T> Future<T> submit(Runnable task, T result) {
                return null;
            }

            @Override
            public Future<?> submit(Runnable task) {
                return null;
            }

            @Override
            public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
                return null;
            }

            @Override
            public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
                return null;
            }

            @Override
            public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
                return null;
            }

            @Override
            public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return null;
            }

            @Override
            public void execute(Runnable command) {

            }
        });
        ThreadFactory defaultThreadFactory = Executors.defaultThreadFactory();
        ThreadFactory privilegedThreadFactory = Executors.privilegedThreadFactory();
        Callable<Object> callable = Executors.callable(new Runnable() {
            @Override
            public void run() {

            }
        });
        Callable<Object> callable2 = Executors.callable(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                return null;
            }
        });
        Callable<Object> callable3 = Executors.callable(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
                return null;
            }
        });
        Callable<Object> privilegedCallable = Executors.privilegedCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return null;
            }
        });
        Callable<Object> privilegedCallableUsingCurrentClassLoader =
                Executors.privilegedCallableUsingCurrentClassLoader(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        return null;
                    }
                });
    }

}
