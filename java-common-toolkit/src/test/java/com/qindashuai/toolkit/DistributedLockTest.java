package com.qindashuai.toolkit;

import com.qindashuai.toolkit.common.BusinessException;
import com.qindashuai.toolkit.common.ResultCode;
import com.qindashuai.toolkit.lock.DistributedLock;
import com.qindashuai.toolkit.lock.DistributedLockAspect;
import com.qindashuai.toolkit.redis.RedisUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DistributedLockTest {

    @Mock
    private RedisUtil redisUtil;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    private DistributedLockAspect aspect;

    @BeforeEach
    void setUp() {
        aspect = new DistributedLockAspect(redisUtil);
    }

    @Test
    void testLockAcquiredSuccessfully() throws Throwable {
        when(redisUtil.tryLockWithWait(anyString(), anyString(), anyLong(), anyLong(), any(TimeUnit.class)))
                .thenReturn(true);
        when(redisUtil.releaseLock(anyString(), anyString())).thenReturn(true);
        when(joinPoint.proceed()).thenReturn("result");

        DistributedLock lock = createLock("testKey", 30000L, 3000L);
        Object result = aspect.around(joinPoint, lock);

        assertEquals("result", result);
        verify(redisUtil).tryLockWithWait(anyString(), anyString(), eq(3000L), eq(30000L), eq(TimeUnit.MILLISECONDS));
        verify(redisUtil).releaseLock(anyString(), anyString());
    }

    @Test
    void testLockAcquireFailed() {
        when(redisUtil.tryLockWithWait(anyString(), anyString(), anyLong(), anyLong(), any(TimeUnit.class)))
                .thenReturn(false);

        DistributedLock lock = createLock("testKey", 30000L, 3000L);

        BusinessException exception = assertThrows(BusinessException.class, () -> aspect.around(joinPoint, lock));
        assertEquals(ResultCode.LOCK_ACQUIRE_FAIL.getCode(), exception.getCode());
    }

    @Test
    void testLockWithCustomMessage() {
        when(redisUtil.tryLockWithWait(anyString(), anyString(), anyLong(), anyLong(), any(TimeUnit.class)))
                .thenReturn(false);

        DistributedLock lock = createLock("testKey", 30000L, 3000L, "自定义锁获取失败消息");

        BusinessException exception = assertThrows(BusinessException.class, () -> aspect.around(joinPoint, lock));
        assertEquals("自定义锁获取失败消息", exception.getMessage());
    }

    @Test
    void testLockReleasedEvenOnException() throws Throwable {
        when(redisUtil.tryLockWithWait(anyString(), anyString(), anyLong(), anyLong(), any(TimeUnit.class)))
                .thenReturn(true);
        when(redisUtil.releaseLock(anyString(), anyString())).thenReturn(true);
        when(joinPoint.proceed()).thenThrow(new RuntimeException("业务异常"));

        DistributedLock lock = createLock("testKey", 30000L, 3000L);

        assertThrows(RuntimeException.class, () -> aspect.around(joinPoint, lock));
        verify(redisUtil).releaseLock(anyString(), anyString());
    }

    private DistributedLock createLock(String key, long leaseTime, long waitTime) {
        return createLock(key, leaseTime, waitTime, "获取分布式锁失败，请稍后重试");
    }

    private DistributedLock createLock(String key, long leaseTime, long waitTime, String errorMessage) {
        return new DistributedLock() {
            @Override
            public String key() { return key; }
            @Override
            public long leaseTime() { return leaseTime; }
            @Override
            public long waitTime() { return waitTime; }
            @Override
            public TimeUnit timeUnit() { return TimeUnit.MILLISECONDS; }
            @Override
            public String errorMessage() { return errorMessage; }
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() { return DistributedLock.class; }
        };
    }
}
