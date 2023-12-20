package dev.padak;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.concurrent.Callable;

public class ExitInterceptor {
    @Advice.OnMethodExit(onThrowable = Exception.class)
    public static void exit(@Advice.Thrown Throwable  throwable , @Origin Method method, @Origin Class<?> clazz) {

        System.out.println("Girdi");
        // Loglama işlemi
        Logger logger = LoggerFactory.getLogger(ExitInterceptor.class);

        if (throwable != null) {
            logger.error("Hata fırladı: {}, Hata sınıfı: {}, Hata metodu: {}", throwable.getMessage(), clazz.getName(), method.getName());
        }
    }

}
