package com.qindashuai.toolkit.lock;

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
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.UUID;

@Slf4j
@Aspect
public class DistributedLockAspect {

    private final RedisUtil redisUtil;

    public DistributedLockAspect(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    private static final ExpressionParser PARSER = new SpelExpressionParser();

    @Around("@annotation(distributedLock)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        String lockKey = buildLockKey(joinPoint, distributedLock.key());
        String requestId = UUID.randomUUID().toString();

        boolean acquired = redisUtil.tryLockWithWait(
                lockKey,
                requestId,
                distributedLock.waitTime(),
                distributedLock.leaseTime(),
                distributedLock.timeUnit()
        );

        if (!acquired) {
            log.warn("获取分布式锁失败: key={}, requestId={}", lockKey, requestId);
            throw new BusinessException(ResultCode.LOCK_ACQUIRE_FAIL, distributedLock.errorMessage());
        }

        log.debug("获取分布式锁成功: key={}, requestId={}", lockKey, requestId);
        try {
            return joinPoint.proceed();
        } finally {
            boolean released = redisUtil.releaseLock(lockKey, requestId);
            if (released) {
                log.debug("释放分布式锁成功: key={}, requestId={}", lockKey, requestId);
            } else {
                log.warn("释放分布式锁失败(可能已超时): key={}, requestId={}", lockKey, requestId);
            }
        }
    }

    private String buildLockKey(ProceedingJoinPoint joinPoint, String keyExpression) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        if (!StringUtils.hasText(keyExpression)) {
            return method.getDeclaringClass().getSimpleName() + ":" + method.getName();
        }

        if (isSpelExpression(keyExpression)) {
            EvaluationContext context = new MethodBasedEvaluationContext(
                    method.getDeclaringClass(),
                    method,
                    args,
                    new DefaultParameterNameDiscoverer()
            );
            Expression expression = PARSER.parseExpression(keyExpression);
            Object value = expression.getValue(context);
            return method.getDeclaringClass().getSimpleName() + ":" + method.getName() + ":" + value;
        }

        return method.getDeclaringClass().getSimpleName() + ":" + method.getName() + ":" + keyExpression;
    }

    private boolean isSpelExpression(String key) {
        return key.contains("#") || key.contains("'");
    }
}
