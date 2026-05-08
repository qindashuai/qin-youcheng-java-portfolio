package com.qindashuai.auth.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RedisUtil {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static final String TOKEN_PREFIX = "auth:token:";
    private static final String REFRESH_TOKEN_PREFIX = "auth:refresh_token:";
    private static final String BLACKLIST_TOKEN_PREFIX = "auth:blacklist:token:";
    private static final String BLACKLIST_IP_PREFIX = "auth:blacklist:ip:";
    private static final String SSO_TICKET_PREFIX = "auth:sso:ticket:";
    private static final String LOGIN_ATTEMPT_PREFIX = "auth:login_attempt:";
    private static final String USER_SESSION_PREFIX = "auth:session:user:";

    public void storeAccessToken(String token, Long userId, String username, String appKey, long expiration) {
        String key = TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(key, userId + ":" + username + ":" + appKey, expiration, TimeUnit.MILLISECONDS);
        redisTemplate.opsForValue().set(USER_SESSION_PREFIX + userId + ":" + appKey, token, expiration, TimeUnit.MILLISECONDS);
    }

    public boolean isAccessTokenExist(String token) {
        String key = TOKEN_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void removeAccessToken(String token) {
        String key = TOKEN_PREFIX + token;
        redisTemplate.delete(key);
    }

    public void removeUserSession(Long userId, String appKey) {
        String key = USER_SESSION_PREFIX + userId + ":" + appKey;
        Object token = redisTemplate.opsForValue().get(key);
        if (token != null) {
            redisTemplate.delete(TOKEN_PREFIX + token);
            redisTemplate.delete(key);
        }
    }

    public String getUserSessionToken(Long userId, String appKey) {
        String key = USER_SESSION_PREFIX + userId + ":" + appKey;
        Object token = redisTemplate.opsForValue().get(key);
        return token != null ? token.toString() : null;
    }

    public void storeRefreshToken(String refreshToken, Long userId, String appKey, long expiration) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        redisTemplate.opsForValue().set(key, userId + ":" + appKey, expiration, TimeUnit.MILLISECONDS);
    }

    public boolean isRefreshTokenExist(String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public String getRefreshTokenInfo(String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? value.toString() : null;
    }

    public void removeRefreshToken(String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        redisTemplate.delete(key);
    }

    public void addToTokenBlacklist(String token, long expiration) {
        String key = BLACKLIST_TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(key, "1", expiration, TimeUnit.MILLISECONDS);
    }

    public boolean isTokenBlacklisted(String token) {
        String key = BLACKLIST_TOKEN_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void addToIpBlacklist(String ip, long expiration) {
        String key = BLACKLIST_IP_PREFIX + ip;
        redisTemplate.opsForValue().set(key, "1", expiration, TimeUnit.MILLISECONDS);
    }

    public boolean isIpBlacklisted(String ip) {
        String key = BLACKLIST_IP_PREFIX + ip;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void removeFromIpBlacklist(String ip) {
        String key = BLACKLIST_IP_PREFIX + ip;
        redisTemplate.delete(key);
    }

    public void storeSsoTicket(String ticket, Long userId, String appKey, long expiration) {
        String key = SSO_TICKET_PREFIX + ticket;
        redisTemplate.opsForValue().set(key, userId + ":" + appKey, expiration, TimeUnit.MILLISECONDS);
    }

    public String getSsoTicketInfo(String ticket) {
        String key = SSO_TICKET_PREFIX + ticket;
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? value.toString() : null;
    }

    public void removeSsoTicket(String ticket) {
        String key = SSO_TICKET_PREFIX + ticket;
        redisTemplate.delete(key);
    }

    public void incrementLoginAttempt(String username) {
        String key = LOGIN_ATTEMPT_PREFIX + username;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, 30, TimeUnit.MINUTES);
        }
    }

    public int getLoginAttemptCount(String username) {
        String key = LOGIN_ATTEMPT_PREFIX + username;
        Object count = redisTemplate.opsForValue().get(key);
        return count != null ? Integer.parseInt(count.toString()) : 0;
    }

    public void resetLoginAttempt(String username) {
        String key = LOGIN_ATTEMPT_PREFIX + username;
        redisTemplate.delete(key);
    }

    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public void delete(Collection<String> keys) {
        redisTemplate.delete(keys);
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public boolean expire(String key, long timeout, TimeUnit unit) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, unit));
    }
}
