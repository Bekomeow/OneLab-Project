package com.example.eventsearchservice.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
public class ControllerLoggingAspect {
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerMethods() {}

    @Before("controllerMethods()")
    public void logRequest(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String username = request.getRemoteUser(); // Получаем имя пользователя
            String ipAddress = request.getRemoteAddr(); // IP адрес клиента
            String url = request.getRequestURL().toString(); // Запрашиваемый URL

            log.info("Запрос: {} {} | IP: {} | Пользователь: {}",
                    request.getMethod(), url, ipAddress, username != null ? username : "Аноним");
        }
    }

    @AfterReturning(pointcut = "controllerMethods()", returning = "response")
    public void logResponse(JoinPoint joinPoint, Object response) {
        log.info("Ответ метода {}: {}", joinPoint.getSignature().getName(), response);
    }

    @AfterThrowing(pointcut = "controllerMethods()", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        log.error("Ошибка в методе {}: {}", joinPoint.getSignature().getName(), ex.getMessage(), ex);
    }
}

