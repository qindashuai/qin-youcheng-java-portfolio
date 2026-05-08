package com.qinyoucheng.toolkit.ratelimit;

import com.qinyoucheng.toolkit.common.BusinessException;
import com.qinyoucheng.toolkit.common.ResultCode;
import com.qinyoucheng.toolkit.redis.RedisUtil;
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
import java.util.Collections;
import java.util.UUID;

@Slf4j
@Aspect
public class RateLimiterAspect {

    private final RedisUtil redisUtil;

    private static final ExpressionParser PARSER = new SpelExpressionParser();

    private static final String RATE_LIMIT_SCRIPT =
            "local key = KEYS[1] " +
            "local permits = tonumber(ARGV[1]) " +
            "local period = tonumber(ARGV[2]) " +
            "local current = tonumber(redis.call('get', key) or '0') " +
            "if current < permits then " +
            "  redis.call('incr', key) " +
            "  if current == 0 then " +
            "    redis.call('expire', key, period) " +
            "  end " +
            "  return 1 " +
            "else " +
            "  return 0 " +
            "end";

    private static final String TOKEN_BUCKET_SCRIPT =
            "local key = KEYS[1] " +
            "local max_permits = tonumber(ARGV[1]) " +
            "local rate = tonumber(ARGV[2]) " +
            "local current_time = tonumber(ARGV[3]) " +
            "local requested = tonumber(ARGV[4]) " +
            "local info = redis.call('hmget', key, 'tokens', 'last_time') " +
            "local tokens = tonumber(info[1]) " +
            "local last_time = tonumber(info[2]) " +
            "if tokens == nil then " +
            "  tokens = max_permits " +
            "  last_time = current_time " +
            "end " +
            "local elapsed = current_time - last_time " +
            "if elapsed > 0 then " +
            "  local new_tokens = elapsed * rate " +
            "  tokens = math.min(max_permits, tokens + new_tokens) " +
            "end " +
            "if tokens >= requested then " +
            "  tokens = tokens - requested " +
            "  redis.call('hmset', key, 'tokens', tokens, 'last_time', current_time) " +
            "  redis.call('expire', key, math.ceil(max_permits / rate) + 1) " +
            "  return 1 " +
            "else " +
            "  redis.call('hmset', key, 'tokens', tokens, 'last_time', current_time) " +
            "  redis.call('expire', key, math.ceil(max_permits / rate) + 1) " +
            "  return 0 " +
            "end";

    public RateLimiterAspect(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    @Around("@annotation(rateLimiter)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimiter rateLimiter) throws Throwable {
        String limitKey = buildLimitKey(joinPoint, rateLimiter);

        long periodSeconds = toSeconds(rateLimiter.period(), rateLimiter.timeUnit());
        double rate = (double) rateLimiter.permits() / periodSeconds;

        boolean allowed = executeTokenBucketScript(limitKey, rateLimiter.permits(), rate, rateLimiter.permits());

        if (!allowed) {
            log.warn("限流拦截: key={}, permits={}, period={}s", limitKey, rateLimiter.permits(), periodSeconds);
            throw new BusinessException(ResultCode.TOO_MANY_REQUESTS, rateLimiter.message());
        }

        return joinPoint.proceed();
    }

    private boolean executeTokenBucketScript(String key, int maxPermits, double rate, int requested) {
        try {
            long currentTime = System.currentTimeMillis() / 1000;
            Long result = redisUtil.getRedisTemplate().execute(
                    new org.springframework.data.redis.core.script.DefaultRedisScript<>(TOKEN_BUCKET_SCRIPT, Long.class),
                    Collections.singletonList(key),
                    String.valueOf(maxPermits),
                    String.valueOf(rate),
                    String.valueOf(currentTime),
                    String.valueOf(requested)
            );
            return result != null && result == 1L;
        } catch (Exception e) {
            log.error("限流脚本执行失败，放行请求: key={}", key, e);
            return true;
        }
    }

    private String buildLimitKey(ProceedingJoinPoint joinPoint, RateLimiter rateLimiter) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = method.getDeclaringClass().getSimpleName();
        String methodName = method.getName();

        StringBuilder keyBuilder = new StringBuilder("rate_limit:");

        switch (rateLimiter.limitType()) {
            case IP:
                keyBuilder.append("ip:").append(getClientIp()).append(":");
                break;
            case USER:
                keyBuilder.append("user:").append(getCurrentUserId()).append(":");
                break;
            case INTERFACE:
                keyBuilder.append("interface:");
                break;
            case CUSTOM:
                keyBuilder.append("custom:");
                if (StringUtils.hasText(rateLimiter.key()) && isSpelExpression(rateLimiter.key())) {
                    EvaluationContext context = new MethodBasedEvaluationContext(
                            method.getDeclaringClass(), method, joinPoint.getArgs(),
                            new DefaultParameterNameDiscoverer()
                    );
                    Expression expression = PARSER.parseExpression(rateLimiter.key());
                    Object value = expression.getValue(context);
                    keyBuilder.append(value).append(":");
                } else if (StringUtils.hasText(rateLimiter.key())) {
                    keyBuilder.append(rateLimiter.key()).append(":");
                }
                break;
        }

        keyBuilder.append(className).append(":").append(methodName);
        return keyBuilder.toString();
    }

    private String getClientIp() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String ip = request.getHeader("X-Forwarded-For");
                if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("X-Real-IP");
                }
                if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }
                if (StringUtils.hasText(ip) && ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        } catch (Exception e) {
            log.debug("获取客户端IP失败", e);
        }
        return UUID.randomUUID().toString();
    }

    private String getCurrentUserId() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String userId = request.getHeader("X-User-Id");
                if (StringUtils.hasText(userId)) {
                    return userId;
                }
            }
        } catch (Exception e) {
            log.debug("获取当前用户ID失败", e);
        }
        return UUID.randomUUID().toString();
    }

    private long toSeconds(long period, RateLimiter.TimeUnit timeUnit) {
        switch (timeUnit) {
            case MINUTES:
                return period * 60;
            case HOURS:
                return period * 3600;
            default:
                return period;
        }
    }

    private boolean isSpelExpression(String key) {
        return key.contains("#") || key.contains("'");
    }
}
