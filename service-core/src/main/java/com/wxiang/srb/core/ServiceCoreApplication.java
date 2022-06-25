package com.wxiang.srb.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.wxiang.srb", "com.wxiang.common"})  // 其它模块的同名包下的组件也可以扫描到
public class ServiceCoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceCoreApplication.class, args);
    }
}


