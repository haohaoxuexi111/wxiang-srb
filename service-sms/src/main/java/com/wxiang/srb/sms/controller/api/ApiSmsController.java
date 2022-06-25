package com.wxiang.srb.sms.controller.api;

import com.wxiang.common.exception.Assert;
import com.wxiang.common.result.R;
import com.wxiang.common.result.ResponseEnum;
import com.wxiang.common.util.RandomUtils;
import com.wxiang.common.util.RegexValidateUtils;
import com.wxiang.srb.sms.client.CoreUserInfoClient;
import com.wxiang.srb.sms.service.SmsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/sms")
@Api(tags = "短信管理")
//@CrossOrigin  // 这是用来解决nginx跨域问题的，gateway不需要，且必须要注释掉
@Slf4j
public class ApiSmsController {

    @Resource
    private SmsService smsService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private CoreUserInfoClient coreUserInfoClient;

    @GetMapping("/send/{mobile}")
    public R send(
            @ApiParam(value = "手机号", required = true)
            @PathVariable String mobile) {
        // 校验手机号码不能为空
        Assert.notEmpty(mobile, ResponseEnum.MOBILE_NULL_ERROR);
        // 判断手机号是否合法
        Assert.isTrue(RegexValidateUtils.checkCellphone(mobile), ResponseEnum.MOBILE_ERROR);

        // 查询手机号是否已经被注册
        boolean mobileExist = coreUserInfoClient.checkMobileExist(mobile);
        Assert.isTrue(!mobileExist, ResponseEnum.MOBILE_EXIST_ERROR); // mobileExist返回true表示手机号已经被注册

        Map<String, Object> map = new HashMap<>();
        String validate = RandomUtils.getFourBitRandom();
        map.put("code", validate);
        // smsService.send(mobile, SmsProperties.TEMPLATE_CODE, map);  // 调用阿里云的短信服务api， 测试阶段用的手机号为：13766816630

        // 将验证码存入redis，用来比对用户输入的验证码
        redisTemplate.opsForValue().set("srb:sms:code:" + mobile, validate, 3, TimeUnit.MINUTES);  // key中不能有空格
        System.out.println("随机生成的6位验证码：" + validate + "，存入redis");

        return R.ok().message("短信发送成功");
    }
}
