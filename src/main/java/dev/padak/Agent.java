package dev.padak;

import org.apache.bcel.Const;
import org.apache.bcel.Constants;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import org.apache.bcel.verifier.structurals.Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class Agent {
    private static final Logger logger = LoggerFactory.getLogger(Agent.class);

    public static void premain(String agentArgs, Instrumentation instrumentation) {

        System.out.println("Java Agent Started");
        instrumentation.addTransformer(new ExceptionLoggingTransformer());
    }

    private static class ExceptionLoggingTransformer implements ClassFileTransformer {
        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {

            String methodName = "";

            try {

                if (className.startsWith("dev/padak/")) {

                    logger.info("Giriş:"+ className);

                    JavaClass javaClass = new ClassParser(new ByteArrayInputStream(classfileBuffer), className + ".java").parse();
                    ClassGen classGen = new ClassGen(javaClass);

                    for (Method method : classGen.getMethods()) {

                        methodName = method.getName();
                        logger.info("Method adı: "+ method.getName());

                        if (method.getCode() != null && !method.getName().equals("<init>")) {

                            MethodGen methodGen = new MethodGen(method, className, classGen.getConstantPool());

                            Method newMethod = addExceptionHandling(methodGen);

                            classGen.replaceMethod(method, newMethod);
                        }
                    }

                    saveJavaFile(classGen, className);
                    return classGen.getJavaClass().getBytes();
                }

            } catch (Exception e) {
                logger.error("class: {} method: {} hata: {}", className, methodName, e);
                return null;
            }

            return null;
        }
    }


    private static Method addExceptionHandling(MethodGen methodGen) throws TargetLostException {

        if (methodGen.getInstructionList() != null) {
            // Check if there is a try-catch block
            CodeExceptionGen[] exceptionHandlers = methodGen.getExceptionHandlers();

            if (exceptionHandlers.length == 0) {

                logger.info("Bu metod'ta tryCatch block yok {} ",methodGen.getName());
;
                Method newMethod = insertTryCatchBlock(methodGen);

                return newMethod;

            } else {
                logger.info("Bu metod'ta tryCatch block var: {} ",methodGen.getName());

                logger.info("Exception sayisi: {}",exceptionHandlers.length);

                for(CodeExceptionGen tek: exceptionHandlers) {

                    logger.info("Exception cesidi {}",tek.getCatchType().getClassName());

                }
            }
        }
        return methodGen.getMethod();
    }

    private static Method insertTryCatchBlock(MethodGen oldmg) {

        InstructionList oldIl = oldmg.getInstructionList();

        InstructionFactory factory = new InstructionFactory(oldmg.getConstantPool());

        // Create Try-Catch block
        InstructionHandle tryStart = oldIl.getStart();
        InstructionHandle tryEnd = oldIl.getEnd();

        ObjectType exceptionType = new ObjectType("java.lang.ArithmeticException");

        // Add catch block
        InstructionList catchList = new InstructionList();
        catchList.append(factory.createPrintln("ArithmeticException occurred!"));

        InstructionHandle catchHandler = oldIl.append(catchList);

        // Add exception type to catch block
        oldmg.addExceptionHandler(tryStart, tryEnd, catchHandler, exceptionType);

        // Update the method with the modified instruction list
        oldmg.setInstructionList(oldIl);

        return oldmg.getMethod();
    }


    private static void saveJavaFile(ClassGen classGen, String className) throws IOException {

        try (FileOutputStream fos = new FileOutputStream("C:\\Users\\Okay Padak\\Desktop\\JavaAgent\\"+className + ".class")) {
            fos.write(classGen.getJavaClass().getBytes());
        } catch (IOException e) {
            logger.error("Error saving .java file for class: {}", className, e);
        }
    }

}
