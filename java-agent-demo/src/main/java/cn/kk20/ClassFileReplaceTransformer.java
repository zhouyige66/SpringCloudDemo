package cn.kk20;

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
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (!className.equals("cn/roy/springcloud/eureka/User")) {
            return classfileBuffer;
        }
        System.out.println("加载器：" + loader);
        System.out.println("加载类：" + className);
        Class<?> aClass = loader.loadClass(className);
        aClass.get
        CtClass ctClass = null;
        try {
            ClassPool pool = ClassPool.getDefault();
//            ctClass = pool.makeClass(new ByteArrayInputStream(classfileBuffer));
            ctClass = pool.get(className.replace("/","."));
            System.out.println("ctClass==" + ctClass.getSimpleName());
            if (!ctClass.isInterface()) {
                CtMethod[] methods = ctClass.getMethods();
                System.out.println("methods：" + methods);
//                CtBehavior[] methods = ctClass.getDeclaredBehaviors();
                for (int i = 0; i < methods.length; i++) {
                    if (!isTargetMethod(methods[i])) {
                        continue;
                    }
                    transformMethod(methods[i]);
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
        if (!"print".equals(method.getName())) {
            return false;
        }
        return true;
    }

    private void transformMethod(CtBehavior method) throws NotFoundException, CannotCompileException {
        System.out.println("Transforming method:" + method.getName());
        method.insertBefore("System.out.println(\"" + method.getName() + "方法插入代码\")");
    }

}
