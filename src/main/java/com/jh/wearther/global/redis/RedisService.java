package com.jh.wearther.global.redis;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisService {

  private final StringRedisTemplate redisTemplate;

  public void setData(String key, String value) {
    redisTemplate.opsForValue().set(key, value);
  }

  public String getData(String key) {
    return redisTemplate.opsForValue().get(key);
  }

  public void setDataExpire(String key, String value, long duration) {
    Duration expireDuration = Duration.ofSeconds(duration);
    redisTemplate.opsForValue().set(key, value, expireDuration);
  }

  public void deleteData(String key) {
    redisTemplate.delete(key);
  }

}
