package org.example.beerProj.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Order(1)
@Component
@RequiredArgsConstructor
public class MeasureAspect {
    private final ObjectMapper objectMapper;

    @Pointcut("within(org.example.beerProj.controller..*)")
    public void anyControllerMethod() {
    }

    @Pointcut("@annotation(org.example.beerProj.annotation.Measure)")
    public void annotatedWithMeasure() {
    }

    @Around("anyControllerMethod() || annotatedWithMeasure()")
    public Object logExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
        var method = ((MethodSignature) pjp.getSignature()).getMethod();
        var className = pjp.getTarget().getClass().getSimpleName();
        var methodName = method.getName();

        String argsJson;
        try {
            argsJson = objectMapper.writeValueAsString(pjp.getArgs());
        } catch (Exception e) {
            argsJson = "[unserializable args]";
        }

        var start = System.currentTimeMillis();
        try {
            var result = pjp.proceed();
            var duration = System.currentTimeMillis() - start;
            log.info("@Measure: \n✓ {}#{} completed in {}ms with args: {}", className, methodName, duration, argsJson);
            return result;
        } catch (Throwable ex) {
            var duration = System.currentTimeMillis() - start;
            log.error("@Measure: \n✗ {}#{} failed in {}ms with args: {}",
                    className,
                    methodName,
                    duration,
                    argsJson,
                    ex);
            throw ex;
        }
    }

}
