package cn.kk20;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;

/**
 * @Description: 热修复
 * @Author: Roy Z
 * @Date: 2020-01-03 15:50
 * @Version: v1.0
 */
public class Hotfix {

    public static void agentmain(String agentArgs, Instrumentation inst) throws UnmodifiableClassException {
        System.out.println("Agent Main called");
        System.out.println("agentArgs : " + agentArgs);
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain, byte[] classfileBuffer)
                    throws IllegalClassFormatException {
                System.out.println("agentmain load Class  :" + className);
                return classfileBuffer;
            }
        }, true);
//        inst.retransformClasses(Account.class);
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
