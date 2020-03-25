package cn.roy.agent;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

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
        for (VirtualMachineDescriptor vmd : list) {
            String displayName = vmd.displayName();
            String id = vmd.id();
            System.out.println("displayName=" + displayName);
            System.out.println("id=" + id);

//            if (vmd.displayName().endsWith("AccountMain")) {
//                VirtualMachine virtualMachine = null;
//                try {
//                    virtualMachine = VirtualMachine.attach(vmd.id());
//                    virtualMachine.loadAgent("E:\\self\\demo\\out\\artifacts\\test\\test.jar", "cxs");
//                    virtualMachine.detach();
//
//                    System.out.println("attach vm成功");
//                } catch (AttachNotSupportedException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (AgentLoadException e) {
//                    e.printStackTrace();
//                } catch (AgentInitializationException e) {
//                    e.printStackTrace();
//                }
//            }
        }
    }

}
