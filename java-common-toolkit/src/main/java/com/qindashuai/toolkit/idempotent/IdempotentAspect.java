package com.qindashuai.toolkit.idempotent;

import com.qindashuai.toolkit.common.BusinessException;
import com.qindashuai.toolkit.common.ResultCode;
import com.qindashuai.toolkit.redis.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.UUID;

@Slf4j
@Aspect
public class IdempotentAspect {

    private final RedisUtil redisUtil;

    private static final ExpressionParser PARSER = new SpelExpressionParser();
    private static final String IDEMPOTENT_PREFIX = "idempotent:";

    public IdempotentAspect(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    @Around("@annotation(idempotent)")
    public Object around(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        String idempotentKey = buildIdempotentKey(joinPoint, idempotent);

        boolean acquired = redisUtil.setIfAbsent(
                idempotentKey,
                "1",
                idempotent.expireTime(),
                idempotent.timeUnit()
        );

        if (!acquired) {
            log.warn("幂等性拦截: key={}", idempotentKey);
            throw new BusinessException(ResultCode.IDEMPOTENT_ERROR, idempotent.message());
        }

        log.debug("幂等性校验通过: key={}", idempotentKey);
        try {
            Object result = joinPoint.proceed();
            if (idempotent.delKey()) {
                redisUtil.delete(idempotentKey);
                log.debug("幂等性Key已删除: key={}", idempotentKey);
            }
            return result;
        } catch (Exception e) {
            redisUtil.delete(idempotentKey);
            log.debug("方法执行异常，删除幂等性Key: key={}", idempotentKey);
            throw e;
        }
    }

    private String buildIdempotentKey(ProceedingJoinPoint joinPoint, Idempotent idempotent) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = method.getDeclaringClass().getSimpleName();
        String methodName = method.getName();

        StringBuilder keyBuilder = new StringBuilder(IDEMPOTENT_PREFIX);

        if (StringUtils.hasText(idempotent.key())) {
            if (isSpelExpression(idempotent.key())) {
                EvaluationContext context = new MethodBasedEvaluationContext(
                        method.getDeclaringClass(), method, joinPoint.getArgs(),
                        new DefaultParameterNameDiscoverer()
                );
                Expression expression = PARSER.parseExpression(idempotent.key());
                Object value = expression.getValue(context);
                keyBuilder.append(className).append(":").append(methodName).append(":").append(value);
            } else {
                keyBuilder.append(idempotent.key());
            }
        } else {
            String requestId = getRequestId();
            keyBuilder.append(className).append(":").append(methodName).append(":").append(requestId);
        }

        return keyBuilder.toString();
    }

    private String getRequestId() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String requestId = request.getHeader("X-Request-Id");
                if (StringUtils.hasText(requestId)) {
                    return requestId;
                }
                String traceId = request.getHeader("X-Trace-Id");
                if (StringUtils.hasText(traceId)) {
                    return traceId;
                }
                String ip = request.getRemoteAddr();
                String uri = request.getRequestURI();
                return ip + ":" + uri;
            }
        } catch (Exception e) {
            log.debug("获取请求标识失败", e);
        }
        return UUID.randomUUID().toString();
    }

    private boolean isSpelExpression(String key) {
        return key.contains("#") || key.contains("'");
    }
}
