package cn.roy.agent;

import com.sun.tools.attach.*;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

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
        System.out.println("pid：" + pid);
        String jarPath = HotfixMain.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String decodePath;
        try {
            decodePath = URLDecoder.decode(jarPath, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            decodePath = jarPath;
        }
        File file = new File(decodePath);
        String dir = file.getParent();
        String agentJarPath = dir + File.separator + "agent.jar";
        System.out.println("agent路径：" + agentJarPath);
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            System.out.println("检查权限");
            sm.checkPermission(new AttachPermission("attachVirtualMachine"));
        } else {
            System.out.println("权限manager为空");
        }
        VirtualMachine machine = null;
        try {
            machine = VirtualMachine.attach(pid);
            machine.loadAgent(agentJarPath);
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
