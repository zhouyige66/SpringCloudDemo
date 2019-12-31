package cn.kk20;

import java.lang.instrument.Instrumentation;

/**
 * @Description: agent
 * @Author: Roy Z
 * @Date: 2019-12-27 15:10
 * @Version: v1.0
 */
public class Agent {
    /**
     * 使用 javaagent 需要几个步骤：
     * 1.定义一个 MANIFEST.MF 文件，必须包含 Premain-Class 选项，通常也会加入Can-Redefine-Classes 和 Can-Retransform-Classes 选项。
     * 2.创建一个Premain-Class 指定的类，类中包含 premain 方法，方法逻辑由用户自己确定。
     * 3.将 premain 的类和 MANIFEST.MF 文件打成 jar 包。
     * 4.使用参数 -javaagent: jar包路径 启动要代理的方法。
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("premain");
        inst.addTransformer(new ClassFileReplaceTransformer(), true);
    }

}
