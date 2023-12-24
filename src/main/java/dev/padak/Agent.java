package dev.padak;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class Agent {
    private static final Logger logger = LoggerFactory.getLogger(Agent.class);

    public static void premain(String agentArgs, Instrumentation instrumentation) {


        instrumentation.addTransformer(new ExceptionLoggingTransformer());
    }

    private static class ExceptionLoggingTransformer implements ClassFileTransformer {
        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {

            String methodName = "";

            try {

                if (className.startsWith("dev/padak/")) { // İlgili sınıfın adını değiştirin

                    logger.info("Giriş:"+ className);

                    JavaClass javaClass = new ClassParser(new ByteArrayInputStream(classfileBuffer), className + ".java").parse();
                    ClassGen classGen = new ClassGen(javaClass);

                    for (Method method : classGen.getMethods()) {

                        methodName = method.getName();
                        logger.info("Method adı: "+ method.getName());

                        if (method.getCode() != null && !method.getName().equals("<init>")) {
                            MethodGen methodGen = new MethodGen(method, className, classGen.getConstantPool());

                            InstructionList instructionList = new InstructionList(methodGen.getMethod().getCode().getCode());

                            // Exception dinleme kodu ve bilgileri alma
                            addExceptionHandling(instructionList, methodGen, javaClass, className, method.getName());

                            methodGen.setMaxStack();
                            methodGen.setMaxLocals();
                            classGen.replaceMethod(method, methodGen.getMethod());
                        }
                    }

                    return classGen.getJavaClass().getBytes();
                }

            } catch (Exception e) {
                logger.error("class: {} method: {} hata: {}", className, methodName, e);
                return null;
            }

            return null;
        }
    }


    private static void addExceptionHandling(InstructionList instructionList, MethodGen methodGen, JavaClass javaClass, String className, String methodName) throws TargetLostException {

        if (instructionList != null) {
            // Check if there is a try-catch block
            CodeExceptionGen[] exceptionHandlers = methodGen.getExceptionHandlers();

            if (exceptionHandlers.length == 0) {
                // If there is no try-catch block, add a new one
                InstructionHandle tryStart = instructionList.getStart();
                InstructionHandle tryEnd = instructionList.getEnd();


                ObjectType exceptionType = new ObjectType("java.lang.Exception");

                logger.info("Bu metod'ta tryCatch block yok {}",methodName);

                InstructionList catchList = new InstructionList();


                ConstantPoolGen constantPoolGen = methodGen.getConstantPool();  // ConstantPoolGen alınır
                int sysOutFieldRef = constantPoolGen.addFieldref("java/lang/System", "out", "Ljava/io/PrintStream;");
                int stringConstRef = constantPoolGen.addString("Exception caught");
                int printlnMethodRef = constantPoolGen.addMethodref("java/io/PrintStream", "println", "(Ljava/lang/String;)V");

                catchList.append(new GETSTATIC(sysOutFieldRef));
                catchList.append(new LDC(stringConstRef));
                catchList.append(new INVOKEVIRTUAL(printlnMethodRef));

                InstructionHandle catchTarget = instructionList.append(catchList);

                methodGen.addExceptionHandler(tryStart,tryEnd,catchTarget,exceptionType);


                Method method = methodGen.getMethod();


            } else {
                logger.info("Bu metod'ta tryCatch block var {}",methodName);

                logger.info("Exception sayisi {}",exceptionHandlers.length);

                for(CodeExceptionGen tek: exceptionHandlers) {

                    logger.info("Exception cesidi {}",tek.getCatchType().getClassName());

                }
            }
        }
    }
}
