package com.wxiang.srb.sms.client.fallback;

import com.wxiang.srb.sms.client.CoreUserInfoClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service  // 当远程服务器调用失败(service-core宕机了)，进行服务熔断处理，回调这个类的对应方法
public class CoreUserInfoClientFallback implements CoreUserInfoClient {
    @Override
    public boolean checkMobileExist(String mobile) {
        log.error("远程调用失败，服务熔断！！！");
        return false;  // 手机号不重复
    }
}
