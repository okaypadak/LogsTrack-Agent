package dev.padak;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

import static org.objectweb.asm.Opcodes.ASM9;
import static org.objectweb.asm.Opcodes.ATHROW;

public class BeforeClassVisitTransformer implements ClassFileTransformer {

    private static final Logger logger = LoggerFactory.getLogger(BeforeClassVisitTransformer.class);
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        ClassReader cr = new ClassReader(classfileBuffer);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassNode classNode = new ClassNode();
        cr.accept(classNode, ClassReader.EXPAND_FRAMES);


        ClassVisitor cv = new ClassVisitor(ASM9, cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                MethodNode mn = new MethodNode(Opcodes.ASM9, access, name, desc, signature, exceptions);



                if ("dev/padak/TryCatchMetod".equals(className)) {

                    System.out.println("Method Node name: "+mn.name);

                    return new BeforeMethodVisitor(mv,  returnNode(classNode, mn.name));
                } else {
                    return mv;
                }
            }
        };

        cr.accept(cv, ClassReader.EXPAND_FRAMES);
        return cw.toByteArray();
    }

    public MethodNode returnNode(ClassNode classNode, String methodName) {
        for (MethodNode methodNode : classNode.methods) {
            if(methodNode.name.equals(methodName)) {
                return methodNode;
            }
        }
        return null;
    }
}