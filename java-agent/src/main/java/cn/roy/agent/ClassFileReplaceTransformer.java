package cn.roy.agent;

import javassist.*;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * @Description: class替换
 * @Author: Roy Z
 * @Date: 2019-12-27 15:11
 * @Version: v1.0
 */
public class ClassFileReplaceTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer)
            throws IllegalClassFormatException {
        String targetClassName = "cn.roy.session.controller.SessionController";
        String replace = targetClassName.replace(".", "/");
        if (className.startsWith(replace)) {
            System.out.println("加载器：" + loader);
            System.out.println("加载类：" + className);
        }
        if (!className.equals(replace)) {
            return classfileBuffer;
        }
        CtClass ctClass = null;
        try {
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(this.getClass()));
            ctClass = pool.makeClass(new ByteArrayInputStream(classfileBuffer));
            System.out.println("ctClass==" + ctClass.getSimpleName());
            if (!ctClass.isInterface()) {
                CtMethod[] methods = ctClass.getMethods();
                System.out.println("methods：" + methods);
                for (int i = 0; i < methods.length; i++) {
                    CtMethod method = methods[i];
                    if (!isTargetMethod(method)) {
                        continue;
                    }

                    transformMethod(method, loader);
                }
                return ctClass.toBytecode();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ctClass != null) {
                ctClass.detach();
            }
        }

        // 返回null则不修改字节码
        return classfileBuffer;
    }

    private boolean isTargetMethod(CtBehavior method) throws NotFoundException {
        return "sessionTest".equals(method.getName());
    }

    private void transformMethod(CtBehavior method, ClassLoader classLoader) throws NotFoundException, CannotCompileException {
        System.out.println("Transforming method:" + method.getName());
        CtClass[] parameterTypes = method.getParameterTypes();
        for (CtClass parameterClass : parameterTypes) {
            try {
                classLoader.loadClass(parameterClass.getClass().getName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        method.setBody("{\n" +
                "        return \"SessionId=\" + $1.getSession().getId();\n" +
                "    }");
    }

}
