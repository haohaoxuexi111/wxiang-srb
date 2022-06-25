package com.wxiang.srb.base.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2Config {

    @Bean
    public Docket adminApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("adminApi")
                .apiInfo(adminApiInfo())
                .select()
                .paths(Predicates.and(PathSelectors.regex("/admin/.*")))  // 按接口路径分组展示
                .build();
    }

    @Bean Docket webApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("webApi")
                .apiInfo(webApiInfo())
                .select()
                .paths(Predicates.and(PathSelectors.regex("/api/.*")))
                .build();
    }

    private ApiInfo adminApiInfo(){  // 文档描述信息
        return new ApiInfoBuilder().title("srb后台管理系统API文档")
                .description("srb后台管理系统各模块的接口调用说明")
                .version("1.6")
                .contact(new Contact("wxiang", "http://www.baidu.com", "2714661705@qq.com"))
                .build();
    }

    private ApiInfo webApiInfo(){
        return new ApiInfoBuilder().title("srb网站API文档")
                .description("srb网站各模块的接口调用说明")
                .version("1.6")
                .contact(new Contact("wxiang", "http://www.baidu.com", "2714661705@qq.com"))
                .build();
    }
}
