package com.qindashuai.toolkit;

import com.qindashuai.toolkit.redis.RedisProperties;
import com.qindashuai.toolkit.redis.RedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisUtilTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @Mock
    private ListOperations<String, Object> listOperations;

    @Mock
    private SetOperations<String, Object> setOperations;

    @Mock
    private ZSetOperations<String, Object> zSetOperations;

    private RedisUtil redisUtil;

    @BeforeEach
    void setUp() {
        RedisProperties properties = new RedisProperties();
        properties.setKeyPrefix("test:");
        redisUtil = new RedisUtil(redisTemplate, properties);
    }

    @Test
    void testSetAndGet() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("test:key1")).thenReturn("value1");

        redisUtil.set("key1", "value1");
        verify(valueOperations).set(eq("test:key1"), eq("value1"));

        Object result = redisUtil.get("key1");
        assertEquals("value1", result);
    }

    @Test
    void testSetWithExpiration() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        redisUtil.set("key1", "value1", 60, TimeUnit.SECONDS);
        verify(valueOperations).set(eq("test:key1"), eq("value1"), eq(60L), eq(TimeUnit.SECONDS));
    }

    @Test
    void testSetIfAbsent() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(eq("test:key1"), eq("value1"), eq(60L), eq(TimeUnit.SECONDS)))
                .thenReturn(true);

        redisUtil.setIfAbsent("key1", "value1", 60, TimeUnit.SECONDS);
        verify(valueOperations).setIfAbsent(eq("test:key1"), eq("value1"), eq(60L), eq(TimeUnit.SECONDS));
    }

    @Test
    void testDelete() {
        when(redisTemplate.delete("test:key1")).thenReturn(true);

        Boolean result = redisUtil.delete("key1");
        assertTrue(result);
    }

    @Test
    void testHasKey() {
        when(redisTemplate.hasKey("test:key1")).thenReturn(true);

        Boolean result = redisUtil.hasKey("key1");
        assertTrue(result);
    }

    @Test
    void testExpire() {
        when(redisTemplate.expire(eq("test:key1"), eq(60L), eq(TimeUnit.SECONDS))).thenReturn(true);

        Boolean result = redisUtil.expire("key1", 60);
        assertTrue(result);
    }

    @Test
    void testIncrement() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment("test:key1")).thenReturn(1L);

        Long result = redisUtil.increment("key1");
        assertEquals(1L, result);
    }

    @Test
    void testDecrement() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.decrement("test:key1")).thenReturn(-1L);

        Long result = redisUtil.decrement("key1");
        assertEquals(-1L, result);
    }

    @Test
    void testHashOperations() {
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);

        redisUtil.hSet("hashKey", "field1", "value1");
        verify(hashOperations).put(eq("test:hashKey"), eq("field1"), eq("value1"));

        Map<String, Object> map = new HashMap<>();
        map.put("field1", "value1");
        redisUtil.hSetAll("hashKey", map);
        verify(hashOperations).putAll(eq("test:hashKey"), eq(map));
    }

    @Test
    void testListOperations() {
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        when(listOperations.leftPush(eq("test:listKey"), eq("value1"))).thenReturn(1L);

        Long result = redisUtil.lPush("listKey", "value1");
        assertEquals(1L, result);
    }

    @Test
    void testSetOperations() {
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(setOperations.add(eq("test:setKey"), any())).thenReturn(1L);

        Long result = redisUtil.sAdd("setKey", "value1");
        assertEquals(1L, result);
    }

    @Test
    void testZSetOperations() {
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.add(eq("test:zsetKey"), eq("value1"), eq(1.0))).thenReturn(true);

        Boolean result = redisUtil.zAdd("zsetKey", "value1", 1.0);
        assertTrue(result);
    }

    @Test
    void testTryLock() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(eq("test:lock:lockKey"), eq("requestId1"), eq(30000L), eq(TimeUnit.MILLISECONDS)))
                .thenReturn(true);

        boolean result = redisUtil.tryLock("lockKey", "requestId1", 30000L);
        assertTrue(result);
    }

    @Test
    void testReleaseLock() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("test:lock:lockKey")).thenReturn("requestId1");
        when(redisTemplate.delete("test:lock:lockKey")).thenReturn(true);

        boolean result = redisUtil.releaseLock("lockKey", "requestId1");
        assertTrue(result);
    }

    @Test
    void testReleaseLockWithWrongRequestId() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("test:lock:lockKey")).thenReturn("requestId1");

        boolean result = redisUtil.releaseLock("lockKey", "wrongRequestId");
        assertFalse(result);
    }
}
