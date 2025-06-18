package com.example.easychat.config;

import org.apache.catalina.filters.CorsFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // 创建跨域配置对象
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*");  // 允许所有来源
        configuration.addAllowedHeader("*");  // 允许所有请求头
        configuration.addAllowedMethod("*");  // 允许所有HTTP方法
        configuration.setAllowCredentials(true); // 允许发送cookie

        // 注册跨域配置
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);  // 适用于所有请求路径

        return source;
    }
}