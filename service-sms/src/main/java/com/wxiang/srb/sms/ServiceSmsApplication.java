package com.wxiang.srb.sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.wxiang.srb", "com.wxiang.common"})  // 其它模块的同名包下的组件也可以扫描到
@EnableFeignClients  // 开启远程服务调用功能，使用注解@EnableFeignClients启用feign客户端；扫描和注册feign客户端bean定义
public class ServiceSmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceSmsApplication.class, args);
    }
}


