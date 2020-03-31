package cn.roy.agent;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Description: 热修复
 * @Author: Roy Z
 * @Date: 2020-01-03 15:50
 * @Version: v1.0
 */
public class HotfixMain {

    public static void main(String[] args) {
        String pid = System.getProperty("pid");
        if (pid == null || pid.trim().length() == 0) {
            return;
        }
        // 读取配置文件夹下需要修复的类
        Properties properties = new Properties();
        // 使用ClassLoader加载properties配置文件生成对应的输入流
        InputStream in = HotfixMain.class.getClassLoader().getResourceAsStream("hotfix.properties");
        // 使用properties对象加载输入流
        VirtualMachine machine = null;
        try {
            properties.load(in);
            String property = properties.getProperty("fix-class-names");
            machine = VirtualMachine.attach(pid);
            machine.loadAgent("/lib/agent.jar", property);
            System.out.println("attach vm success");
        } catch (AttachNotSupportedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AgentLoadException e) {
            e.printStackTrace();
        } catch (AgentInitializationException e) {
            e.printStackTrace();
        } finally {
            if (machine != null) {
                try {
                    machine.detach();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
