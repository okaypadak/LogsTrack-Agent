package dev.padak;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ATHROW;

public class BeforeMethodVisitor extends MethodVisitor {

    private static final Logger logger = LoggerFactory.getLogger(BeforeMethodVisitor.class);
    private int tryCatchCount = 0;
    private final String methodName;
    private final MethodNode methodNode;


    public BeforeMethodVisitor(MethodVisitor mv, MethodNode methodNode) {
        super(ASM9, mv);
        this.methodName = methodNode.name;
        this.methodNode = methodNode;
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        super.visitTryCatchBlock(start, end, handler, type);
        //tryCatchCount++;
        //System.out.println("visitTryCatchBlock'a method:"+methodName);
        //System.out.println("Try-Catch Block: start=" + start + ", end=" + end + ", handler=" + handler + ", type=" + type);


    }

    @Override
    public void visitCode() {
        super.visitCode();

        logger.info("Hello method: " + methodName);
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("Hello method: " + methodName);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

        hasThrowNew(methodNode);

    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }

    /*    @Override
        public void visitInsn(int opcode) {
            if (opcode == ATHROW) {

                System.out.println("Found a 'throw new' expression in method: " + methodName);
            }
            super.visitInsn(opcode);
        }*/
    public static void hasThrowNew(MethodNode methodNode) {
        System.out.println("Girdi "+ methodNode.name);
        for (TryCatchBlockNode tryCatchBlock : methodNode.tryCatchBlocks) {
            for (AbstractInsnNode insnNode = tryCatchBlock.handler.getNext(); insnNode != null; insnNode = insnNode.getNext()) {
                if (insnNode.getOpcode() == ATHROW) {
                    System.out.println("-- Found 'throw new' in handler: " + insnNode);

                }
            }
        }
    }

    public static void addTryCatchBlock(TryCatchBlockNode tryCatchBlock) {
        System.out.println("Başlangıç-Bitiş" + tryCatchBlock.start + " " + tryCatchBlock.end);
    }


}