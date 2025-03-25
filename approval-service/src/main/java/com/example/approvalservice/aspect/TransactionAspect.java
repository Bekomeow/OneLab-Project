package com.example.approvalservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class TransactionAspect {

    @Around("bean(*Service*)")
    public Object manageTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Начало транзакции для метода: " + joinPoint.getSignature().getName());
        Object result;
        try {
            result = joinPoint.proceed();
            log.info("Транзакция успешно выполнена.");
        } catch (Exception e) {
            log.error("Откат транзакции из-за ошибки: " + e.getMessage());
            throw e;
        }
        return result;
    }
}
