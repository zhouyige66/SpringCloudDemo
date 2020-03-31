package cn.roy.agent;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

/**
 * @Description: agent
 * @Author: Roy Z
 * @Date: 2019-12-27 15:10
 * @Version: v1.0
 */
public class Agent {
    /**
     * 使用 javaagent 需要几个步骤：
     * 1.定义一个 MANIFEST.MF 文件，必须包含 Premain-Class 选项，通常也会加入 Can-Redefine-Classes 和 Can-Retransform-Classes 选项
     * 2.创建一个 Premain-Class 指定的类，类中包含 premain 方法，方法逻辑由用户自己确定
     * 3.将 premain 的类和 MANIFEST.MF 文件打成jar包
     * 4.使用参数 -javaagent: jar包路径 启动要代理的方法
     */
    public static void premain(String agentArgs, Instrumentation instrumentation) {
        System.out.println("调用了premain方法");
        instrumentation.addTransformer(new ClassFileReplaceTransformer(true), true);
    }

    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        System.out.println("调用了agentmain方法");
        final ClassFileReplaceTransformer transformer = new ClassFileReplaceTransformer(false);
        instrumentation.addTransformer(transformer, true);
        final String[] replaceClassNames = transformer.getReplaceClassNames();
        if (replaceClassNames == null || replaceClassNames.length == 0) {
            return;
        }
        final Class[] allLoadedClasses = instrumentation.getAllLoadedClasses();
        try {
            for (Class clazz : allLoadedClasses) {
                for (String str : replaceClassNames) {
                    if (str.equals(clazz.getName())) {
                        System.out.println("找到需要修复的类：" + clazz.getName());
                        instrumentation.retransformClasses(clazz);
                    }
                }
            }
        } catch (UnmodifiableClassException e) {
            e.printStackTrace();
        }
    }

}
