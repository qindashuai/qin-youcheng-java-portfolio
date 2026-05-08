package com.qinyoucheng.rag.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperationLog operationLog) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String module = operationLog.module();
        String desc = operationLog.value();
        Object[] args = joinPoint.getArgs();

        log.info("[操作日志] 模块:{} 操作:{} 类:{} 方法:{} 参数:{}",
                module, desc, className, methodName, Arrays.toString(args));

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long costTime = System.currentTimeMillis() - startTime;
            log.info("[操作日志] 模块:{} 操作:{} 类:{} 方法:{} 耗时:{}ms 结果:成功",
                    module, desc, className, methodName, costTime);
            return result;
        } catch (Throwable e) {
            long costTime = System.currentTimeMillis() - startTime;
            log.error("[操作日志] 模块:{} 操作:{} 类:{} 方法:{} 耗时:{}ms 结果:失败 异常:{}",
                    module, desc, className, methodName, costTime, e.getMessage());
            throw e;
        }
    }
}
