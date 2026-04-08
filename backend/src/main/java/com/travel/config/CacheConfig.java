package com.travel.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Redis + Caffeine 多级缓存配置
 * 
 * 缓存策略：
 * - 一级缓存（Caffeine）：热点数据，本地快速访问
 * - 二级缓存（Redis）：分布式缓存，跨服务共享
 */
@Configuration
@EnableCaching
public class CacheConfig {

    // ==================== Caffeine 本地缓存配置 ====================
    
    public static final String CAFFEINE_CACHE_MANAGER = "caffeineCacheManager";
    public static final String SPOT_CACHE = "spotCache";           // 景点详情
    public static final String HOTEL_CACHE = "hotelCache";         // 酒店详情
    public static final String ARTICLE_CACHE = "articleCache";     // 游记详情
    public static final String USER_CACHE = "userCache";           // 用户信息

    @Bean(CAFFEINE_CACHE_MANAGER)
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000)                    // 最多缓存1000条
                .expireAfterWrite(10, TimeUnit.MINUTES) // 写入后10分钟过期
                .recordStats());                       // 开启统计
        return cacheManager;
    }

    // ==================== Redis 分布式缓存配置 ====================
    
    public static final String REDIS_CACHE_MANAGER = "redisCacheManager";

    @Bean(REDIS_CACHE_MANAGER)
    @Primary
    public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        // 默认缓存配置
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))           // 默认30分钟
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();                 // 不缓存null值

        // 各缓存独立配置
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // 景点列表缓存 - 5分钟
        cacheConfigurations.put("spots", defaultConfig.entryTtl(Duration.ofMinutes(5)));
        
        // 景点详情缓存 - 10分钟
        cacheConfigurations.put("spotDetail", defaultConfig.entryTtl(Duration.ofMinutes(10)));
        
        // 热门景点缓存 - 1小时
        cacheConfigurations.put("hotSpots", defaultConfig.entryTtl(Duration.ofHours(1)));
        
        // 酒店列表缓存 - 5分钟
        cacheConfigurations.put("hotels", defaultConfig.entryTtl(Duration.ofMinutes(5)));
        
        // 酒店详情缓存 - 10分钟
        cacheConfigurations.put("hotelDetail", defaultConfig.entryTtl(Duration.ofMinutes(10)));
        
        // 游记列表缓存 - 5分钟
        cacheConfigurations.put("articles", defaultConfig.entryTtl(Duration.ofMinutes(5)));
        
        // 游记详情缓存 - 30分钟
        cacheConfigurations.put("articleDetail", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        
        // 用户信息缓存 - 15分钟
        cacheConfigurations.put("userInfo", defaultConfig.entryTtl(Duration.ofMinutes(15)));
        
        // 订单缓存（支付中）- 30分钟
        cacheConfigurations.put("orderCache", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        
        // 搜索热词缓存 - 1小时
        cacheConfigurations.put("searchHotWords", defaultConfig.entryTtl(Duration.ofHours(1)));
        
        // 统计概览缓存 - 5分钟
        cacheConfigurations.put("statsOverview", defaultConfig.entryTtl(Duration.ofMinutes(5)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }

    // ==================== Redis Template 配置 ====================
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // Key 序列化
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        
        // Value 序列化
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        
        template.afterPropertiesSet();
        return template;
    }
}
