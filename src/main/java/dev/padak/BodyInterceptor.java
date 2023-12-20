package dev.padak;

import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class BodyInterceptor {

    @RuntimeType
    public static Object intercept(@Origin Method method, @SuperCall Callable<?> callable) {

        Logger logger = LoggerFactory.getLogger(ExitInterceptor.class);

        try {
            logger.debug("intercept method'e giriş");
            long start = System.currentTimeMillis();
            try {
                return callable.call();
            } finally {
                long executionTime = System.currentTimeMillis() - start;
                logger.info("{} {} ms sürdü", method, executionTime);
            }
        } catch (Exception e) {
            logger.error("Method çalıştırılırken hata oluştu", e);
            return null;
        }
    }
}
