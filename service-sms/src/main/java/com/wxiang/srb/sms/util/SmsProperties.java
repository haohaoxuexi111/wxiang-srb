package com.wxiang.srb.sms.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "aliyun.sms")
public class SmsProperties implements InitializingBean {
    private String regionId;
    private String keyId;
    private String keySecret;
    private String templateCode;
    private String signName;

    // 上面变量读取到配置文件中的值之后，赋给下面的静态变量，方便后续调用
    public static String REGION_ID;
    public static String KEY_ID;
    public static String KEY_SECRET;
    public static String TEMPLATE_CODE;
    public static String SIGN_NAME;

    // 当私有成员变量被赋值之后，此方法自动被调用，从而初始化常量
    @Override
    public void afterPropertiesSet() throws Exception {
        REGION_ID = regionId;
        KEY_ID = keyId;
        KEY_SECRET = keySecret;
        TEMPLATE_CODE = templateCode;
        SIGN_NAME = signName;
    }
}
