package org.example.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {
    @Before("execution(* org.example.repository.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Вызов метода: {}", joinPoint.getSignature().getName());
    }

    @AfterReturning(pointcut = "execution(* org.example.repository.*.*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("Метод {} вернул: {}", joinPoint.getSignature().getName(), result);
    }

    @AfterThrowing(pointcut = "execution(* org.example.repository.*.*(..))", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        log.error("Ошибка в методе {}: {}", joinPoint.getSignature().getName(), ex.getMessage(), ex);
    }
}
