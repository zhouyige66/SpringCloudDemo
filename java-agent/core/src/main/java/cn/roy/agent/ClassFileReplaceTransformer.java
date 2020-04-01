package cn.roy.agent;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;

import java.io.*;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.net.URLDecoder;
import java.security.ProtectionDomain;
import java.util.Properties;

/**
 * @Description: class替换
 * @Author: Roy Z
 * @Date: 2019-12-27 15:11
 * @Version: v1.0
 */
public class ClassFileReplaceTransformer implements ClassFileTransformer {
    private boolean isPremain;
    private String parentFilePath;
    private String[] replaceClassNames;

    public ClassFileReplaceTransformer(boolean isPremain) {
        this.isPremain = isPremain;
        init();
    }

    private void init() {
        // 读取配置文件夹下需要修复的类
        Properties properties = new Properties();
        // 使用ClassLoader加载properties配置文件生成对应的输入流
        String jarPath = Agent.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String decodePath;
        try {
            decodePath = URLDecoder.decode(jarPath, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            decodePath = jarPath;
        }
        File file = new File(decodePath);
        parentFilePath = file.getParent();
        String propPath = parentFilePath + File.separator + "conf" + File.separator + "hotfix.properties";
        System.out.println("配置文件路径：" + propPath);
        try (
                FileInputStream fileInputStream = new FileInputStream(propPath);
                InputStream in = new BufferedInputStream(fileInputStream)) {
            // 使用properties对象加载输入流
            properties.load(in);
            String property = properties.getProperty("fix-class-names");
            System.out.println("读取需要替换的类为：" + property);
            if (property != null) {
                replaceClassNames = property.trim().split(",");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer)
            throws IllegalClassFormatException {
        if (replaceClassNames == null || replaceClassNames.length == 0) {
            return null;
        }
        boolean replace = isPremain ? false : true;
        if (isPremain) {
            String name = className.replace("/", ".");
            for (String str : replaceClassNames) {
                if (str.equals(name)) {
                    replace = true;
                    break;
                }
            }
        }
        if (replace) {
            // 查找需要替换的类文件是否存在
            String fileName = className.substring(className.lastIndexOf("/") + 1);
            String path = parentFilePath + File.separator + "res" + File.separator + fileName + ".class";
            System.out.println("修复类文件路径：" + path);
            File file = new File(path);
            if (!file.exists()) {
                System.out.println("文件不存在");
                return null;
            }

            ClassPool classPool = ClassPool.getDefault();
            classPool.appendClassPath(new ClassClassPath(this.getClass()));
            System.out.println("获取classPool成功");
            CtClass ctClass = null;
            try {
                ctClass = classPool.makeClass(new DataInputStream(new FileInputStream(file)));
                System.out.println("加载修复类成功");
                return ctClass.toBytecode();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (CannotCompileException e) {
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

    public String[] getReplaceClassNames() {
        return replaceClassNames;
    }
}
