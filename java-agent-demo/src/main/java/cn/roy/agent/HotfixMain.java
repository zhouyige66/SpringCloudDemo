package cn.roy.agent;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.io.IOException;

/**
 * @Description: 热修复
 * @Author: Roy Z
 * @Date: 2020-01-03 15:50
 * @Version: v1.0
 */
public class HotfixMain {

    public static void main(String[] args) {
        String pid = System.getProperty("pid");
        String agentPath = System.getProperty("agentPath");
        if (pid == null || pid.trim().length() == 0 || agentPath == null || agentPath.trim().length() == 0) {
            return;
        }

        VirtualMachine machine = null;
        try {
            machine = VirtualMachine.attach(pid);
            machine.loadAgent(agentPath, "agentmain");
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
