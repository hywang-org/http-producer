package com.flash.message.config.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.flash.message.utils.RedisOperationSets;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Repository
public class AppInfoRedis extends RedisOperationSets {

    @Resource(name = "redis_app")
    private RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void Redis1() {
        super.setRedisTemplate(redisTemplate);
    }
}
