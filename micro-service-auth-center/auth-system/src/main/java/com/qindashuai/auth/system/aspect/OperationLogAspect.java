package com.qindashuai.auth.system.aspect;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.qindashuai.auth.common.UserContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    @Resource
    private com.qindashuai.auth.system.service.OperationLogService operationLogService;

    @Around("@annotation(com.qindashuai.auth.system.aspect.OperationLog)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        OperationLog annotation = method.getAnnotation(OperationLog.class);

        com.qindashuai.auth.system.entity.SysOperationLog logEntity = new com.qindashuai.auth.system.entity.SysOperationLog();
        logEntity.setTraceId(IdUtil.fastSimpleUUID());
        logEntity.setOperationType(annotation.operationType());
        logEntity.setOperationDesc(annotation.value());

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            logEntity.setRequestMethod(request.getMethod());
            logEntity.setRequestUrl(request.getRequestURI());
            logEntity.setIp(getClientIp(request));
            logEntity.setAppKey(request.getHeader("X-App-Key"));
        }

        Long userId = UserContextHolder.getUserId();
        String username = UserContextHolder.getUsername();
        logEntity.setUserId(userId);
        logEntity.setUsername(username);

        try {
            String params = JSONUtil.toJsonStr(point.getArgs());
            logEntity.setRequestParams(params.length() > 2000 ? params.substring(0, 2000) : params);
        } catch (Exception e) {
            logEntity.setRequestParams("参数序列化失败");
        }

        Object result = null;
        try {
            result = point.proceed();
            logEntity.setStatus(1);
            try {
                String responseJson = JSONUtil.toJsonStr(result);
                logEntity.setResponseResult(responseJson.length() > 2000 ? responseJson.substring(0, 2000) : responseJson);
            } catch (Exception e) {
                logEntity.setResponseResult("响应序列化失败");
            }
        } catch (Throwable e) {
            logEntity.setStatus(0);
            String errorMsg = e.getMessage();
            logEntity.setErrorMsg(errorMsg != null && errorMsg.length() > 2000 ? errorMsg.substring(0, 2000) : errorMsg);
            throw e;
        } finally {
            logEntity.setDuration(System.currentTimeMillis() - startTime);
            try {
                operationLogService.saveLog(logEntity);
            } catch (Exception e) {
                log.error("保存操作日志失败", e);
            }
        }

        return result;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            int index = ip.indexOf(',');
            return index != -1 ? ip.substring(0, index) : ip;
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }
}
