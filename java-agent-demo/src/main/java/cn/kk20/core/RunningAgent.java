package cn.kk20.core;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

/**
 * @Description: 热修复
 * @Author: Roy Z
 * @Date: 2020-01-03 15:50
 * @Version: v1.0
 */
public class RunningAgent {

    public static void agentmain(String agentArgs, Instrumentation inst) throws UnmodifiableClassException {
        System.out.println("Agent Main called");
        System.out.println("agentArgs : " + agentArgs);
        inst.addTransformer(new ClassFileReplaceTransformer(), true);
        // 类替换
        Class<?> account = null;
        try {
            account = Class.forName("Account");
            inst.retransformClasses(account);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args)
//            throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
//        List<VirtualMachineDescriptor> list = VirtualMachine.list();
//        for (VirtualMachineDescriptor vmd : list) {
//            if (vmd.displayName().endsWith("AccountMain")) {
//                VirtualMachine virtualMachine = VirtualMachine.attach(vmd.id());
//                virtualMachine.loadAgent("E:\\self\\demo\\out\\artifacts\\test\\test.jar ", "cxs");
//                System.out.println("ok");
//                virtualMachine.detach();
//            }
//        }
//    }

}
