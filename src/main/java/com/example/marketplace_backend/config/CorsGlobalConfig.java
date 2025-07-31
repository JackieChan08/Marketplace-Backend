package com.example.marketplace_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsGlobalConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true); // разрешает куки и авторизацию
        config.addAllowedOriginPattern("*"); // разрешает все origin (например, localhost:3000)
        config.addAllowedHeader("*"); // разрешает любые заголовки
        config.addAllowedMethod("*"); // разрешает любые HTTP-методы: GET, POST и т.д.

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // применяется ко всем URL

        return new CorsFilter(source);
    }
}