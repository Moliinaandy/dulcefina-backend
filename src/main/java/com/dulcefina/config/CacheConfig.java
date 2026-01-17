package com.dulcefina.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching // Habilita el soporte de caché de Spring
public class CacheConfig {

    public static final String VERIFICATION_CODE_CACHE = "verificationCodes";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(VERIFICATION_CODE_CACHE);
        cacheManager.setCaffeine(Caffeine.newBuilder()
                // Los códigos expirarán 10 minutos después de ser escritos en el caché
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(1000) // Limita el tamaño del caché
        );
        return cacheManager;
    }
}