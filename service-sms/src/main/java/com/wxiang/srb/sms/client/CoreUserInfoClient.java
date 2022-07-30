package com.wxiang.srb.sms.client;

import com.wxiang.common.result.R;
import com.wxiang.srb.sms.client.fallback.CoreUserInfoClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// 远程调用客户端，使用注解@FeignClient 定义feign客户端; 该例子定义了一个feign客户端，将远程服务http://localhost:8110/api/core/userInfo/checkMobile/{mobile}映射为一个本地Java方法调用。
@FeignClient(value = "service-core", fallback = CoreUserInfoClientFallback.class)  // 指定要调用的远程服务以及服务降级时回调的类
public interface CoreUserInfoClient {  // 这个对象会代理客户端完成远程服务方法的调用，可以想使用本地服务一样，使用@Autowired注入到spring容器中

    // 要映射远程api服务的完整路径，调用这个方法想当于调用service-core模块的同名api方法
    @GetMapping("/api/core/userInfo/checkMobile/{mobile}")
    boolean checkMobileExist(@PathVariable String mobile);

}



