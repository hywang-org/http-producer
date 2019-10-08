package com.flash.message.config.redis;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.flash.message.utils.RedisOperationSets;

@Repository
public class UserInfoRedis extends RedisOperationSets {

    @Resource(name = "redis_user")
    private RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void Redis1() {
        super.setRedisTemplate(redisTemplate);
    }
}
