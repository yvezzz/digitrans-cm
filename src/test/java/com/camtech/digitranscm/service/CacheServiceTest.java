package com.camtech.digitranscm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CacheServiceTest {

    @Mock private RedisTemplate<String, Object> redisTemplate;
    @Mock private RedisConnectionFactory connectionFactory;
    @Mock private RedisConnection connection;
    @Mock private ValueOperations<String, Object> valueOps;

    @InjectMocks
    private CacheService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void put_cachesInRedis_whenOnline() {
        when(redisTemplate.getConnectionFactory()).thenReturn(connectionFactory);
        when(connectionFactory.getConnection()).thenReturn(connection);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);

        service.put("test-key", "test-value");

        verify(valueOps).set("test-key", "test-value", 30, TimeUnit.MINUTES);
    }

    @Test
    void get_returnsFromRedis_whenAvailable() {
        when(redisTemplate.getConnectionFactory()).thenReturn(connectionFactory);
        when(connectionFactory.getConnection()).thenReturn(connection);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get("test-key")).thenReturn("cached-value");

        Object result = service.get("test-key");

        assertThat(result).isEqualTo("cached-value");
    }

    @Test
    void get_fallsBackToLocal_whenRedisDown() {
        when(redisTemplate.getConnectionFactory()).thenReturn(connectionFactory);
        when(connectionFactory.getConnection()).thenThrow(new RuntimeException("Connection refused"));

        service.put("local-key", "local-value");
        Object result = service.get("local-key");
        assertThat(result).isEqualTo("local-value");
    }

    @Test
    void isOnline_returnsFalse_whenRedisUnavailable() {
        when(redisTemplate.getConnectionFactory()).thenReturn(connectionFactory);
        when(connectionFactory.getConnection()).thenThrow(new RuntimeException("No connection"));

        assertThat(service.isOnline()).isFalse();
    }

    @Test
    void evict_removesFromRedis() {
        when(redisTemplate.getConnectionFactory()).thenReturn(connectionFactory);
        when(connectionFactory.getConnection()).thenReturn(connection);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);

        service.evict("some-key");

        verify(redisTemplate).delete("some-key");
    }

    @Test
    void hasKey_checksRedis_whenOnline() {
        when(redisTemplate.getConnectionFactory()).thenReturn(connectionFactory);
        when(connectionFactory.getConnection()).thenReturn(connection);
        when(redisTemplate.hasKey("existing")).thenReturn(true);

        assertThat(service.hasKey("existing")).isTrue();
    }
}
