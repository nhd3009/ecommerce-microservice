package com.nhd.order_service.config;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(String.class, LocalDateTime.class, source -> {
            if (source == null || source.isBlank()) return null;
            if (source.length() == 10) {
                return LocalDate.parse(source).atStartOfDay();
            }
            return LocalDateTime.parse(source);
        });
    }
}
