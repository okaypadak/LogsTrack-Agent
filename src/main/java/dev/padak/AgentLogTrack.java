package dev.padak;

import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;

import static org.objectweb.asm.Opcodes.ATHROW;

public class AgentLogTrack {
    private static final Logger logger = LoggerFactory.getLogger(AgentLogTrack.class);

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        System.out.println("::: Java Agent Started :::");
        instrumentation.addTransformer(new BeforeClassVisitTransformer());
    }


}
