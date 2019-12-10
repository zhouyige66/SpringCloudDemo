package cn.roy.springcloud.mq;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

/**
 * @Description: 代理测试
 * @Author: Roy Z
 * @Date: 2019-09-13 09:56
 * @Version: v1.0
 */
public class ProxyTest {

    public static void main(String[] args) {
        LogHandler logHandler = new LogHandler();

        UserManager manager = (UserManager) logHandler.newProxyInstance(new UserClient());
        manager.addUser("roy");
        manager.addUser("kk20");
        manager.addUser("yige");
        manager.removeUser("yige");
        manager.removeUser("test");
        manager.print();
    }

    public static class LogHandler implements InvocationHandler {
        // 目标对象
        private Object targetObject;

        // 绑定关系，也就是关联到哪个接口（与具体的实现类绑定）的哪些方法将被调用时，执行invoke方法。
        public Object newProxyInstance(Object targetObject) {
            this.targetObject = targetObject;
            // 该方法用于为指定类装载器、一组接口及调用处理器生成动态代理类实例
            // 第一个参数指定产生代理对象的类加载器，需要将其指定为和目标对象同一个类加载器
            // 第二个参数要实现和目标对象一样的接口，所以只需要拿到目标对象的实现接口
            // 第三个参数表明这些被拦截的方法在被拦截时需要执行哪个InvocationHandler的invoke方法
            // 根据传入的目标返回一个代理对象
            return Proxy.newProxyInstance(targetObject.getClass().getClassLoader(),
                    targetObject.getClass().getInterfaces(), this);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (args != null && args.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    System.out.println("传递参数：" + args[i]);
                }
            } else {
                System.out.println("无参方法");
            }
            Object ret = null;
            try {
                /*原对象方法调用前处理日志信息*/
                System.out.println("start-->>");
                // 调用目标方法
                ret = method.invoke(targetObject, args);
                /*原对象方法调用后处理日志信息*/
                System.out.println("success-->>");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("error-->>");
                throw e;
            }
            return ret;
        }
    }

    public interface UserManager {
        boolean addUser(String name);

        boolean removeUser(String name);

        void print();
    }

    public static class UserClient implements UserManager {
        private Set<String> nameSet = new HashSet<>();

        @Override
        public boolean addUser(String name) {
            return nameSet.add(name);
        }

        @Override
        public boolean removeUser(String name) {
            return nameSet.remove(name);
        }

        @Override
        public void print() {
            System.out.println(nameSet);
        }
    }

}
