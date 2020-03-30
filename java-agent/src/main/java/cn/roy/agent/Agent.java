package cn.roy.agent;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("premain");
        inst.addTransformer(new ClassFileReplaceTransformer(), true);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        System.out.println("调用了agentmain方法");
        Class[] allLoadedClasses = inst.getAllLoadedClasses();
        for (Class item : allLoadedClasses) {
            System.out.println("已加载类：" + item.getName());
        }

        inst.addTransformer(new ClassFileReplaceTransformer(), true);
        try {
            // 类替换
            Class<?> clazz = Class.forName("cn.roy.session.controller.SessionController");
            inst.retransformClasses(clazz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (UnmodifiableClassException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

    }

}
