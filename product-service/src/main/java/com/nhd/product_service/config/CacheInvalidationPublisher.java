package com.nhd.product_service.config;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
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
public class CacheInvalidationPublisher {

    private final StringRedisTemplate redisTemplate;
    private final ChannelTopic cacheInvalidationTopic;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public void publish(CacheInvalidationEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(cacheInvalidationTopic.getTopic(), json);
            log.info("=======> Published cache invalidation event: {}", json);
        } catch (Exception e) {
            log.error("=======> Failed to publish cache invalidation event: {}", e.getMessage(), e);
        }
    }
}
