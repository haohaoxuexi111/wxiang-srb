package com.wxiang.srb.base.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// openfeign远程服务调用日志级别配置，共有四个日志级别：
// NONE：默认级别，不显示日志；
// BASIC：仅记录请求方法、URL、响应状态及执行时间；
// HEADERS：除了BASIC中定义的信息之外，还有请求和响应头信息；
// FULL：除了HEADERS中定义的信息之外，还有请求和响应正文及元数据信息。
@Configuration
public class OpenFeignConfig {
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
    // 此外，远程服务消费者接口也需要定义成DEBUG级别
}
