package com.wxiang.srb.sms.client;

import com.wxiang.common.result.R;
import com.wxiang.srb.sms.client.fallback.CoreUserInfoClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// 远程调用客户端
@FeignClient(value = "service-core", fallback = CoreUserInfoClientFallback.class)  // 指定要调用的远程服务以及服务降级时回调的类
public interface CoreUserInfoClient {

    // 要映射远程api服务的完整路径，调用这个方法想当于调用service-core模块的同名api方法
    @GetMapping("/api/core/userInfo/checkMobile/{mobile}")
    boolean checkMobileExist(@PathVariable String mobile);

}



