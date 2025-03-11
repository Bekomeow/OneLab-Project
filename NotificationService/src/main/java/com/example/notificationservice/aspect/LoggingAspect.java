package com.example.notificationservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(* com.example.notificationservice.repository.*.*(..))")
    public void repositoryMethods() {}

    @Before("repositoryMethods()")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Вызов метода: {}", joinPoint.getSignature().getName());
    }

    @AfterReturning(pointcut = "repositoryMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("Метод {} вернул: {}", joinPoint.getSignature().getName(), result);
    }

    @AfterThrowing(pointcut = "repositoryMethods()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        log.error("Ошибка в методе {}: {}", joinPoint.getSignature().getName(), ex.getMessage(), ex);
    }
}

