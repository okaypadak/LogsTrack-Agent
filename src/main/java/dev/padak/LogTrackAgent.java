package dev.padak;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import org.objectweb.asm.*;

public class LogTrackAgent {
    private static final Logger logger = LoggerFactory.getLogger(LogTrackAgent.class);

    public static void premain(String agentArgs, Instrumentation instrumentation) {

        System.out.println("Java Agent Started");
        instrumentation.addTransformer(new ExceptionLoggingTransformer());
    }

    private static class ExceptionLoggingTransformer implements ClassFileTransformer {
        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {

            ClassReader cr = new ClassReader(classfileBuffer);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

            ClassVisitor cv = new ClassVisitor(Opcodes.ASM5, cw) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                    if ("dev/padak/TryCatchMetod".equals(className)) {
                        return new MethodVisitor(Opcodes.ASM7, mv) {
                            @Override
                            public void visitCode() {
                                super.visitCode();
                                // Replace System.out.println("Merhaba metod"); with System.out.println("Hello method");
                                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                                mv.visitLdcInsn("Hello method: "+name);
                                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
                            }
                        };
                    } else {
                        return mv;
                    }
                }
            };

            cr.accept(cv, ClassReader.EXPAND_FRAMES);
            return cw.toByteArray();
        }
    }

    }
