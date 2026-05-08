package com.qindashuai.toolkit.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties properties;

    public RedisUtil(RedisTemplate<String, Object> redisTemplate, RedisProperties properties) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
    }

    private String wrapKey(String key) {
        return properties.getKeyPrefix() + key;
    }

    // ==================== String Operations ====================

    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(wrapKey(key), value);
    }

    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(wrapKey(key), value, timeout, unit);
    }

    public void set(String key, Object value, long seconds) {
        set(key, value, seconds, TimeUnit.SECONDS);
    }

    public void setIfAbsent(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().setIfAbsent(wrapKey(key), value, timeout, unit);
    }

    public Boolean setIfAbsent(String key, Object value) {
        return redisTemplate.opsForValue().setIfAbsent(wrapKey(key), value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) redisTemplate.opsForValue().get(wrapKey(key));
    }

    public String getString(String key) {
        Object value = get(key);
        return value != null ? value.toString() : null;
    }

    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(wrapKey(key));
    }

    public Boolean delete(String key) {
        return redisTemplate.delete(wrapKey(key));
    }

    public Long delete(Collection<String> keys) {
        @SuppressWarnings("unchecked")
        Collection<String> wrappedKeys = (Collection<String>) CollectionUtils.arrayToList(
                keys.stream().map(this::wrapKey).toArray());
        return redisTemplate.delete(wrappedKeys);
    }

    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(wrapKey(key), timeout, unit);
    }

    public Boolean expire(String key, long seconds) {
        return expire(key, seconds, TimeUnit.SECONDS);
    }

    public Long getExpire(String key) {
        return redisTemplate.getExpire(wrapKey(key));
    }

    public Long getExpire(String key, TimeUnit unit) {
        return redisTemplate.getExpire(wrapKey(key), unit);
    }

    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(wrapKey(key));
    }

    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(wrapKey(key), delta);
    }

    public Long decrement(String key) {
        return redisTemplate.opsForValue().decrement(wrapKey(key));
    }

    public Long decrement(String key, long delta) {
        return redisTemplate.opsForValue().decrement(wrapKey(key), delta);
    }

    // ==================== Hash Operations ====================

    public void hSet(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(wrapKey(key), hashKey, value);
    }

    public void hSetAll(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(wrapKey(key), map);
    }

    @SuppressWarnings("unchecked")
    public <T> T hGet(String key, String hashKey) {
        return (T) redisTemplate.opsForHash().get(wrapKey(key), hashKey);
    }

    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(wrapKey(key));
    }

    public void hDelete(String key, Object... hashKeys) {
        redisTemplate.opsForHash().delete(wrapKey(key), hashKeys);
    }

    public Boolean hHasKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(wrapKey(key), hashKey);
    }

    public Long hSize(String key) {
        return redisTemplate.opsForHash().size(wrapKey(key));
    }

    public Long hIncrement(String key, String hashKey, long delta) {
        return redisTemplate.opsForHash().increment(wrapKey(key), hashKey, delta);
    }

    public Set<Object> hKeys(String key) {
        return redisTemplate.opsForHash().keys(wrapKey(key));
    }

    public List<Object> hValues(String key) {
        return redisTemplate.opsForHash().values(wrapKey(key));
    }

    // ==================== List Operations ====================

    public Long lPush(String key, Object value) {
        return redisTemplate.opsForList().leftPush(wrapKey(key), value);
    }

    public Long lPushAll(String key, Object... values) {
        return redisTemplate.opsForList().leftPushAll(wrapKey(key), values);
    }

    public Long rPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(wrapKey(key), value);
    }

    public Long rPushAll(String key, Object... values) {
        return redisTemplate.opsForList().rightPushAll(wrapKey(key), values);
    }

    @SuppressWarnings("unchecked")
    public <T> T lPop(String key) {
        return (T) redisTemplate.opsForList().leftPop(wrapKey(key));
    }

    @SuppressWarnings("unchecked")
    public <T> T rPop(String key) {
        return (T) redisTemplate.opsForList().rightPop(wrapKey(key));
    }

    @SuppressWarnings("unchecked")
    public <T> T lIndex(String key, long index) {
        return (T) redisTemplate.opsForList().index(wrapKey(key), index);
    }

    public Long lSize(String key) {
        return redisTemplate.opsForList().size(wrapKey(key));
    }

    public List<Object> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(wrapKey(key), start, end);
    }

    public void lTrim(String key, long start, long end) {
        redisTemplate.opsForList().trim(wrapKey(key), start, end);
    }

    // ==================== Set Operations ====================

    public Long sAdd(String key, Object... values) {
        return redisTemplate.opsForSet().add(wrapKey(key), values);
    }

    public Long sRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(wrapKey(key), values);
    }

    public Set<Object> sMembers(String key) {
        return redisTemplate.opsForSet().members(wrapKey(key));
    }

    public Boolean sIsMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(wrapKey(key), value);
    }

    public Long sSize(String key) {
        return redisTemplate.opsForSet().size(wrapKey(key));
    }

    public Set<Object> sIntersect(String key1, String key2) {
        return redisTemplate.opsForSet().intersect(wrapKey(key1), wrapKey(key2));
    }

    public Set<Object> sUnion(String key1, String key2) {
        return redisTemplate.opsForSet().union(wrapKey(key1), wrapKey(key2));
    }

    public Set<Object> sDifference(String key1, String key2) {
        return redisTemplate.opsForSet().difference(wrapKey(key1), wrapKey(key2));
    }

    @SuppressWarnings("unchecked")
    public <T> T sRandomMember(String key) {
        return (T) redisTemplate.opsForSet().randomMember(wrapKey(key));
    }

    // ==================== ZSet Operations ====================

    public Boolean zAdd(String key, Object value, double score) {
        return redisTemplate.opsForZSet().add(wrapKey(key), value, score);
    }

    public Long zRemove(String key, Object... values) {
        return redisTemplate.opsForZSet().remove(wrapKey(key), values);
    }

    public Double zScore(String key, Object value) {
        return redisTemplate.opsForZSet().score(wrapKey(key), value);
    }

    public Long zRank(String key, Object value) {
        return redisTemplate.opsForZSet().rank(wrapKey(key), value);
    }

    public Long zReverseRank(String key, Object value) {
        return redisTemplate.opsForZSet().reverseRank(wrapKey(key), value);
    }

    public Set<Object> zRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(wrapKey(key), start, end);
    }

    public Set<Object> zReverseRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(wrapKey(key), start, end);
    }

    public Set<ZSetOperations.TypedTuple<Object>> zRangeWithScores(String key, long start, long end) {
        return redisTemplate.opsForZSet().rangeWithScores(wrapKey(key), start, end);
    }

    public Long zSize(String key) {
        return redisTemplate.opsForZSet().size(wrapKey(key));
    }

    public Long zCount(String key, double min, double max) {
        return redisTemplate.opsForZSet().count(wrapKey(key), min, max);
    }

    public Long zRemoveRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().removeRange(wrapKey(key), start, end);
    }

    public Long zRemoveRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().removeRangeByScore(wrapKey(key), min, max);
    }

    // ==================== Distributed Lock ====================

    public boolean tryLock(String lockKey, String requestId, long leaseTime, TimeUnit unit) {
        String wrappedKey = wrapKey("lock:" + lockKey);
        Boolean result = redisTemplate.opsForValue().setIfAbsent(wrappedKey, requestId, leaseTime, unit);
        return Boolean.TRUE.equals(result);
    }

    public boolean tryLock(String lockKey, String requestId, long leaseTime) {
        return tryLock(lockKey, requestId, leaseTime, TimeUnit.MILLISECONDS);
    }

    public boolean releaseLock(String lockKey, String requestId) {
        String wrappedKey = wrapKey("lock:" + lockKey);
        Object currentValue = redisTemplate.opsForValue().get(wrappedKey);
        if (currentValue != null && currentValue.equals(requestId)) {
            Boolean deleted = redisTemplate.delete(wrappedKey);
            return Boolean.TRUE.equals(deleted);
        }
        return false;
    }

    public boolean tryLockWithWait(String lockKey, String requestId, long waitTime, long leaseTime, TimeUnit unit) {
        long startTime = System.currentTimeMillis();
        long waitMillis = unit.toMillis(waitTime);
        while (true) {
            if (tryLock(lockKey, requestId, leaseTime, unit)) {
                return true;
            }
            if (System.currentTimeMillis() - startTime >= waitMillis) {
                return false;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
    }

    // ==================== Utility ====================

    public RedisTemplate<String, Object> getRedisTemplate() {
        return this.redisTemplate;
    }
}
