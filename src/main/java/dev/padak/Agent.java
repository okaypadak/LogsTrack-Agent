package dev.padak;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.Instrumentation;

public class Agent {

    private static final ElementMatcher<? super String> BYTE_BUDDY_LOGGING_FILTER;

    static {
        String filterClass = System.getProperty("bytebuddy.debug.instrumentation.for.class");
        BYTE_BUDDY_LOGGING_FILTER = filterClass != null ? s -> s.contains(filterClass) : s -> false;
    }


    private static void mode4(Instrumentation instrumentation) {

        AgentBuilder.Listener listener = new AgentBuilder.Listener() {
            @Override
            public void onDiscovery(String s, ClassLoader classLoader, JavaModule javaModule, boolean b) {

            }

            @Override
            public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule, boolean b, DynamicType dynamicType) {

            }

            @Override
            public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule, boolean b) {

            }

            @Override
            public void onError(String s, ClassLoader classLoader, JavaModule javaModule, boolean b, Throwable throwable) {

            }

            @Override
            public void onComplete(String s, ClassLoader classLoader, JavaModule javaModule, boolean b) {
                System.out.println("Completed: " + s);
            }
        };

        new AgentBuilder.Default()
                .type(ElementMatchers.any())
                .transform((builder, type, classLoader, module) ->
                        builder.method(ElementMatchers.any())
                                .intercept(Advice.to(ExitInterceptor.class))
                )
                .with(listener)
                .installOn(instrumentation);
    }

    private static void mode5(Instrumentation instrumentation) {
        new AgentBuilder.Default()
                .with(AgentBuilder.Listener.StreamWriting.toSystemOut())
                //.disableClassFormatChanges().with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                //.with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .type(ElementMatchers.any())
                .transform((builder, typeDescription, classLoader, module) -> builder
                        .visit(Advice.to(ExitInterceptor.class).on(ElementMatchers.any()))
                )
                .installOn(instrumentation);
    }
    private static void mode1(Instrumentation instrumentation) {
        new AgentBuilder.Default()
                .type(ElementMatchers.any())
                .transform((builder, type, classLoader, module) -> builder.method(
                        ElementMatchers.any()).intercept(MethodDelegation.to(BodyInterceptor.class))
                ).with(new AgentBuilder.Listener.Filtering(BYTE_BUDDY_LOGGING_FILTER, AgentBuilder.Listener.StreamWriting.toSystemOut()
                )).installOn(instrumentation);
    }

    private static void mode3(Instrumentation instrumentation) {
        new AgentBuilder.Default()
                //.type(ElementMatchers.any())
                .type(ElementMatchers.any())
                .transform((builder, type, classLoader, module) ->
                        builder.method(ElementMatchers.any()).intercept(MethodDelegation.to(BodyInterceptor.class))
                ).installOn(instrumentation);
    }

    private static void mode2(Instrumentation instrumentation) {

        new AgentBuilder.Default()
                .disableClassFormatChanges()
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .with(AgentBuilder.Listener.StreamWriting.toSystemOut())
                .type(ElementMatchers.any())
                //.type(ElementMatchers.nameMatches(".*doProcess.*"))
                //.type(ElementMatchers.hasSuperType(ElementMatchers.named("com.example.bytebuddylab.app.AbstractBusinessClass")))
                .transform((builder, type, classLoader, module) ->
                        builder.visit(Advice.to(TimerAdvice.class).on(ElementMatchers.any()))
                ).installOn(instrumentation);

    }

    public static void premain(String arguments, Instrumentation instrumentation) {

        System.out.println(" ******* Agent starts ******* ");
        mode5(instrumentation);

    }

}
