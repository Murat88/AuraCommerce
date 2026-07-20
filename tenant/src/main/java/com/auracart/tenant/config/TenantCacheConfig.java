package com.auracart.tenant.config;

import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;

/**
 * Tenant çözümleme (resolution) sırasında her istekte veritabanına
 * gitmemek için aktif tenant listesini Redis üzerinde cache'leyen konfigürasyon.
 * <p>
 * Cache provider olarak Redis kullanılır; kayıtlar {@code TTL} süresi boyunca
 * taze kabul edilir, bu sürenin sonunda otomatik olarak yenilenir (tekrar DB'den çekilir).
 * Redis, bellek içi (Caffeine gibi) çözümlerin aksine uygulamanın birden fazla
 * instance'ı arasında paylaşılan/kalıcı bir cache sağlar.
 */
@Configuration
@EnableCaching
public class TenantCacheConfig {

    private static final Duration TTL = Duration.ofMinutes(10);

    /**
     * {@link TenantCacheNames#TENANTS} cache'i için TTL ve serileştirme
     * (anahtar: String, değer: JSON) ayarlarını özelleştirir.
     */
    @Bean
    public RedisCacheManagerBuilderCustomizer tenantRedisCacheManagerBuilderCustomizer() {
        RedisCacheConfiguration tenantsCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(TTL)
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(RedisSerializer.string()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(RedisSerializer.json()));

        return builder -> builder.withCacheConfiguration(TenantCacheNames.TENANTS, tenantsCacheConfig);
    }
}

