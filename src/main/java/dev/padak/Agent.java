package dev.padak;

import org.apache.bcel.Constants;
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
                            addExceptionHandling(instructionList, methodGen, javaClass, className, method, method.getName());

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


    private static void addExceptionHandling(InstructionList instructionList, MethodGen methodGen, JavaClass javaClass, String className, Method method, String methodName) throws TargetLostException {

        if (instructionList != null) {
            // Check if there is a try-catch block
            CodeExceptionGen[] exceptionHandlers = methodGen.getExceptionHandlers();

            if (exceptionHandlers.length == 0) {

                logger.info("Bu metod'ta tryCatch block yok {}",methodName);
;
                Code code = method.getCode();
                //InstructionList instructionList = new InstructionList(code.getCode());

                // Orijinal bytecode'ları saklamak için yeni bir instruction list oluştur
                InstructionList originalInstructions = new InstructionList(code.getCode());

                // Yöntemin içine try-catch bloğunu ekle
                createTryCatchBlock(methodGen);
                //instructionList.insert(tryCatchBlock);

                // Yeni bytecode'ları yönteme set et
                //code.setCode(instructionList.getByteCode());

                // Yöntemin boyutunu güncelle
                //methodGen.getMethod().getCode().setCode(code.getCode());

                logger.info("sonuç: "+String.valueOf(methodGen.getMethod().getCode())+"\n");

                logger.info("sonuç: "+methodGen.getMethod().getCode().getCode()+"\n");


            } else {
                logger.info("Bu metod'ta tryCatch block var {}",methodName);

                logger.info("Exception sayisi {}",exceptionHandlers.length);

                for(CodeExceptionGen tek: exceptionHandlers) {

                    logger.info("Exception cesidi {}",tek.getCatchType().getClassName());

                }
            }
        }
    }

    private static void createTryCatchBlock(MethodGen methodGen) {

        InstructionList il = methodGen.getInstructionList();
        InstructionHandle start = il.getStart();
        InstructionHandle end = il.getEnd();

        InstructionFactory factory = new InstructionFactory(methodGen.getConstantPool());

        InstructionList originalInstructions = new InstructionList();
        originalInstructions.append(new PUSH(methodGen.getConstantPool(), "Hello, World!"));
        originalInstructions.append(factory.createInvoke("java.lang.System", "println", Type.VOID, new Type[]{Type.STRING}, Constants.INVOKESTATIC));

        il.append(originalInstructions);

        InstructionList tryCatchIL = new InstructionList();

        InstructionHandle ih = tryCatchIL.append(factory.createInvoke("java.lang.System", "println", Type.VOID, new Type[]{Type.STRING}, Constants.INVOKESTATIC));

        ObjectType exceptionType = new ObjectType("java/lang/Exception");

        CodeExceptionGen handler = methodGen.addExceptionHandler(start, end, ih, exceptionType);

        il.insert(start, tryCatchIL);

    }
}
