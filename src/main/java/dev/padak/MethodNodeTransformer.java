package dev.padak;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodNodeTransformer extends ClassNode implements Opcodes {

    private static final Logger logger = LoggerFactory.getLogger(MethodNodeTransformer.class);

    public MethodNodeTransformer() {
        super(ASM9);
    }

    public static byte[] transform(byte[] classBytes) {

        ClassReader cr = new ClassReader(classBytes);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassNode cn = new ClassNode(ASM9);
        cr.accept(cn, 0);

        System.out.println("Node Class name "+cn.name);

        if(cn.name.equals("dev/padak/TryCatchMetod")) {

            for (MethodNode mn : cn.methods) {

                System.out.println("Node Method name "+mn.name);

                InsnList insnList = new InsnList();

                insnList.add(new MethodInsnNode(INVOKESTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
                insnList.add(new LdcInsnNode("hello method"));
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "print", "(Ljava/lang/String;)V", false));

                mn.instructions.insertBefore(mn.instructions.getFirst(), insnList);


            }


            cn.accept(cw);
            return cw.toByteArray();
        } else {
            return classBytes;
        }







    }

}