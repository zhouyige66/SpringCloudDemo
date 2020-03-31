package cn.roy.agent;

import javassist.ClassPool;
import javassist.CtClass;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
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
        String redefinedName = classBeingRedefined.getName();
        System.out.println("重定义的类：" + redefinedName);
        System.out.println("加载器：" + loader);
        System.out.println("加载类：" + className);
        String replace = className.replace("/", ".");
        if (replace.equals(redefinedName)) {
            CtClass ctClass = null;
            // 查找需要替换的类文件是否存在
            String name = redefinedName.substring(redefinedName.lastIndexOf(".") + 1);
            String path = "hotfix" + File.separator + name + ".class";
            System.out.println("path = " + path);
            File file = new File(path);
            if (!file.exists()) {
                return null;
            }

            try {
                ClassPool pool = ClassPool.getDefault();
                ctClass = pool.makeClass(new DataInputStream(
                        new FileInputStream("/Users/zzy/Desktop/SessionController.class")));
                return ctClass.toBytecode();
//            ClassPool pool = ClassPool.getDefault();
//            pool.insertClassPath(new ClassClassPath(this.getClass()));
//            ctClass = pool.makeClass(new ByteArrayInputStream(classfileBuffer));
//            System.out.println("ctClass==" + ctClass.getSimpleName());
//            if (!ctClass.isInterface()) {
//                CtMethod[] methods = ctClass.getMethods();
//                System.out.println("methods：" + methods);
//                for (int i = 0; i < methods.length; i++) {
//                    CtMethod method = methods[i];
//                    if (!isTargetMethod(method)) {
//                        continue;
//                    }
//
//                    transformMethod(method, loader);
//                }
//                return ctClass.toBytecode();
//            }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (ctClass != null) {
                    ctClass.detach();
                }
            }
        }

        // 返回null则不修改字节码
        return null;
    }

}
