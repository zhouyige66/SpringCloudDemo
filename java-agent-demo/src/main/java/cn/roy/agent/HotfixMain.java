package cn.roy.agent;

import com.sun.tools.attach.*;

import java.io.IOException;
import java.util.List;

/**
 * @Description: 热修复
 * @Author: Roy Z
 * @Date: 2020-01-03 15:50
 * @Version: v1.0
 */
public class HotfixMain {

    public static void main(String[] args) {
        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        String pid = System.getProperty("pid");
        String agentPath = System.getProperty("agentPath");
        System.out.println("获取的pid：" + pid);
        System.out.println("获取的agent路径：" + agentPath);
        for (VirtualMachineDescriptor vmd : list) {
            String displayName = vmd.displayName();
            String id = vmd.id();
            System.out.println("displayName=" + displayName);
            System.out.println("id=" + id);
            if (id.equals(pid)) {
                VirtualMachine virtualMachine = null;
                try {
                    virtualMachine = VirtualMachine.attach(id);
                    virtualMachine.loadAgent(agentPath);
                    virtualMachine.detach();
                    System.out.println("attach vm成功");
                    break;
                } catch (AttachNotSupportedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (AgentLoadException e) {
                    e.printStackTrace();
                } catch (AgentInitializationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
