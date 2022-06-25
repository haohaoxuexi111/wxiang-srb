package com.wxiang.srb.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

// 解决前端访问网关时出现的跨域问题
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);  // 是否允许携带cookie
        corsConfiguration.addAllowedOrigin("*");  // 可接受的域，是一个具体的域名或者 * (任意域名)
        corsConfiguration.addAllowedHeader("*");  // 允许携带的头
        corsConfiguration.addAllowedMethod("*");  // 允许的访问方式

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();  // 别导错包了
        source.registerCorsConfiguration("/**", corsConfiguration);  // 配置生效的路径

        return new CorsWebFilter(source);
    }

}
