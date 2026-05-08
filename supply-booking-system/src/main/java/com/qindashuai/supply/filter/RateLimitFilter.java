package com.qindashuai.supply.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qindashuai.supply.common.Result;
import com.qindashuai.supply.common.ResultCode;
import com.qindashuai.supply.config.RateLimitConfig;
import com.qindashuai.supply.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class RateLimitFilter implements Filter {

    private final RedisUtil redisUtil;
    private final RateLimitConfig rateLimitConfig;
    private final ObjectMapper objectMapper;

    private static final String RATE_LIMIT_KEY_PREFIX = "supply:rate_limit:";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!rateLimitConfig.isEnabled()) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String clientId = getClientId(httpRequest);
        String key = RATE_LIMIT_KEY_PREFIX + clientId;

        boolean allowed = tryAcquire(key);
        if (!allowed) {
            log.warn("接口限流: clientId={}, uri={}", clientId, httpRequest.getRequestURI());
            writeRateLimitResponse(response);
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean tryAcquire(String key) {
        long currentTime = System.currentTimeMillis();
        String lastRefillKey = key + ":last_refill";
        String tokensKey = key + ":tokens";

        Object tokensObj = redisUtil.get(tokensKey);
        Object lastRefillObj = redisUtil.get(lastRefillKey);

        int tokens = tokensObj != null ? Integer.parseInt(tokensObj.toString()) : rateLimitConfig.getDefaultTokens();
        long lastRefillTime = lastRefillObj != null ? Long.parseLong(lastRefillObj.toString()) : currentTime;

        long elapsed = currentTime - lastRefillTime;
        int newTokens = (int) (elapsed / 1000.0 * rateLimitConfig.getDefaultRate());
        tokens = Math.min(rateLimitConfig.getDefaultCapacity(), tokens + newTokens);

        if (tokens > 0) {
            tokens--;
            redisUtil.set(tokensKey, tokens, 60, TimeUnit.SECONDS);
            redisUtil.set(lastRefillKey, currentTime, 60, TimeUnit.SECONDS);
            return true;
        }

        return false;
    }

    private String getClientId(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private void writeRateLimitResponse(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS);
        Result<Void> result = Result.fail(ResultCode.RATE_LIMIT_EXCEEDED);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
