package com.qinyoucheng.supply.aspect;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    @Around("@annotation(com.qinyoucheng.supply.aspect.OperationLog)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        OperationLog operationLog = method.getAnnotation(OperationLog.class);

        String module = operationLog.module();
        String operation = operationLog.operation();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String ip = "";
        String requestMethod = "";
        String requestUri = "";
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            ip = getIpAddress(request);
            requestMethod = request.getMethod();
            requestUri = request.getRequestURI();
        }

        String params = "";
        Object[] args = point.getArgs();
        if (args != null && args.length > 0) {
            try {
                params = JSONUtil.toJsonStr(args);
                if (params.length() > 500) {
                    params = params.substring(0, 500) + "...";
                }
            } catch (Exception e) {
                params = "参数序列化失败";
            }
        }

        Object result;
        try {
            result = point.proceed();
        } catch (Exception e) {
            log.error("操作异常 - 模块: {}, 操作: {}, URI: {}, IP: {}, 耗时: {}ms, 异常: {}",
                    module, operation, requestUri, ip, System.currentTimeMillis() - startTime, e.getMessage());
            throw e;
        }

        long costTime = System.currentTimeMillis() - startTime;
        log.info("操作日志 - 模块: {}, 操作: {}, 方法: {} {}, IP: {}, 参数: {}, 耗时: {}ms",
                module, operation, requestMethod, requestUri, ip, params, costTime);

        return result;
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StrUtil.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            }
            return ip;
        }
        ip = request.getHeader("X-Real-IP");
        if (StrUtil.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }
}
