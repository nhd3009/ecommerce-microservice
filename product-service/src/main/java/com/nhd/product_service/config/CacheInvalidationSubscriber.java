package com.nhd.product_service.config;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhd.product_service.dto.CacheInvalidationEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CacheInvalidationSubscriber implements MessageListener {

    private final CacheManager cacheManager;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String json = new String(message.getBody());
            CacheInvalidationEvent event = objectMapper.readValue(json, CacheInvalidationEvent.class);

            log.info("Received cache invalidation event: {}", json);

            if ("CATEGORY_SERVICE".equals(event.getSource())) {
                handleCategoryEvent(event);
            }

        } catch (Exception e) {
            log.error("Failed to process cache invalidation event: {}", e.getMessage(), e);
        }
    }

    private void handleCategoryEvent(CacheInvalidationEvent event) {
        switch (event.getType()) {
            case "CATEGORY_UPDATED", "CATEGORY_DELETED" -> {
                clearCacheIfExists("products_by_category");
                clearCacheIfExists("product");
                clearCacheIfExists("product_pages");
                log.info("Cleared product caches due to category event: {}", event.getType());
            }
            default -> log.warn("Unknown category event type: {}", event.getType());
        }
    }

    private void clearCacheIfExists(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            log.info("Cleared cache '{}'", cacheName);
        } else {
            log.warn("Cache '{}' not found in CacheManager", cacheName);
        }
    }
}
