package com.example.Shoe_shop.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Log4j2
public class RedisCacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Object getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void setValue(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        }
        catch (Exception e) {
            log.error("SetValue: "+e.getMessage());
        }
    }

    public <T> T getValue(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) return null;
            return objectMapper.convertValue(value, clazz);
        } catch (Exception e) {
            log.error("getValue: "+e.getMessage());
            return null;
        }
    }

    public void setTimeOut(String key, long timeout, TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }

    public void setValueWithTimeout(String key, Object value, long ttl, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForValue().set(key, value);
            redisTemplate.expire(key,ttl, timeUnit);
        } catch (Exception e) {
            log.error("setValueWithTimeout: "+e.getMessage());
        }

    }

    public boolean checkExistsKey(String key){
        boolean check = false;
        try {
            check = redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("checkExistsKey: "+e.getMessage());
        }
        return check;
    }

    public void lPushAll(String key, List<String> value) {
        try{
            redisTemplate.opsForList().leftPushAll(key, value);
        }
        catch (Exception e){
            log.error("lPushAll: "+e.getMessage());
        }
    }

    public void lPush(String key, Object value) {
        try{
            redisTemplate.opsForList().leftPush(key, value);
        }
        catch (Exception e){
            log.error("lPush: "+e.getMessage());
        }
    }

    public Object rPop(String key) {
        try{
            return redisTemplate.opsForList().rightPop(key);
        }
        catch (Exception e){
            log.error("rPop: "+e.getMessage());
            return null;
        }
    }
    public void deleteKey(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("deleteKey: "+e.getMessage());
        }
    }

}
