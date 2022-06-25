package com.wxiang.srb.core.controller.api;


import com.alibaba.fastjson.JSON;
import com.wxiang.common.result.R;
import com.wxiang.srb.base.util.JwtUtils;
import com.wxiang.srb.core.hfb.RequestHelper;
import com.wxiang.srb.core.pojo.vo.UserBindVO;
import com.wxiang.srb.core.service.UserBindService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 用户绑定表 前端控制器
 * </p>
 *
 * @author wxiang
 * @since 2022-04-20
 */
@Api(tags="会员账号绑定")
@RestController
@RequestMapping("/api/core/userBind")
@Slf4j
public class UserBindController {

    @Resource
    private UserBindService userBindService;

    @ApiOperation("提交账户绑定数据")
    @PostMapping("/auth/bind")  // 所有需要登录才能执行的功能都添加了 "/auth/" 网关可以根据路径进行校验
    public R bind(@RequestBody UserBindVO userBindVO, HttpServletRequest request) {

        // 从header中获取token，并对token进行校验，确保用户已经登录，并从token中提取userId
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        // 根据userId做账户绑定，生成一个动态表单的字符串
        String formStr = userBindService.commitBindUser(userBindVO, userId);

        return R.ok().data("formStr", formStr);
    }

    @ApiOperation("绑定异步回调")
    @PostMapping("/notify")
    public String notify(HttpServletRequest request) {
        // hfb向srb发起回调请求时携带的参数
        Map<String, Object> paramMap = RequestHelper.switchMap(request.getParameterMap());
        log.info("hfb账户绑定回调srb时携带的参数如下：" + JSON.toJSONString(paramMap));

        // 验签，校验hfb传过来的sign与我们生成的sign是否相同
        if (!RequestHelper.isSignEquals(paramMap)) {
            log.error("用户账号绑定异步回调签名验证错误：" + JSON.toJSONString(paramMap));
            return "fail";
        }

        log.info("验签成功，开始账户绑定");
        userBindService.notify(paramMap);

        return "success";
    }

}

