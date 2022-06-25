package com.wxiang.srb.core.controller.api;


import com.wxiang.common.exception.Assert;
import com.wxiang.common.result.R;
import com.wxiang.common.result.ResponseEnum;
import com.wxiang.common.util.RegexValidateUtils;
import com.wxiang.srb.base.util.JwtUtils;
import com.wxiang.srb.core.pojo.vo.LoginVO;
import com.wxiang.srb.core.pojo.vo.RegisterVO;
import com.wxiang.srb.core.pojo.vo.UserInfoVO;
import com.wxiang.srb.core.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 用户基本信息 前端控制器
 * </p>
 *
 * @author wxiang
 * @since 2022-04-20
 */
@Api(tags = "会员接口")
@RestController
@RequestMapping("/api/core/userInfo")
@Slf4j
//@CrossOrigin  // 这是用来解决nginx跨域问题的，gateway不需要，且必须要注释掉
public class UserInfoController {

    @Resource
    private RedisTemplate redisTemplate;
//    @Resource
//    private RedisTemplate<String, String> redisTemplate1;

    @Resource
    private UserInfoService userInfoService;

    @ApiOperation(("会员注册"))
    @PostMapping("/register")
    public R register(@RequestBody RegisterVO registerVO) {

        String mobile = registerVO.getMobile();
        String code = registerVO.getCode();
        String password = registerVO.getPassword();

        Assert.notEmpty(mobile, ResponseEnum.MOBILE_NULL_ERROR);
        Assert.notEmpty(code, ResponseEnum.CODE_NULL_ERROR);
        Assert.notEmpty(password, ResponseEnum.PASSWORD_NULL_ERROR);
        Assert.isTrue(RegexValidateUtils.checkCellphone(mobile), ResponseEnum.MOBILE_ERROR);

        // 校验验证码是否正确
        String codeGen = (String)redisTemplate.opsForValue().get("srb:sms:code:" + mobile);
        // 从redis中取出的是带双引号的json字符串 "123456"，而不是String类型的 123456，所以无法使用String泛型的redisTemplate接收
        // String coded = redisTemplate1.opsForValue().get("srb:sms:code:" + mobile);

        Assert.equals(code, codeGen, ResponseEnum.CODE_ERROR);

        // 注册
        userInfoService.register(registerVO);
        return R.ok().message("注册成功");
    }

    @ApiOperation("会员登录")
    @PostMapping("/login")
    public R login(@RequestBody LoginVO loginVO, HttpServletRequest request) {
        String mobile = loginVO.getMobile();
        String password = loginVO.getPassword();

        Assert.notEmpty(mobile, ResponseEnum.MOBILE_NULL_ERROR);
        Assert.notEmpty(password, ResponseEnum.PASSWORD_NULL_ERROR);

        String ip = request.getRemoteAddr();// 获取客户端地址
        UserInfoVO userInfoVO = userInfoService.login(loginVO, ip);  // 返回用户信息给前端
        return R.ok().data("userInfo", userInfoVO);  // 在前端服务器中会将得到的userInfo放到cookie中
    }

    @ApiOperation("校验令牌")
    @GetMapping("/checkToken")
    public R checkToken(HttpServletRequest request) {
        String token = request.getHeader("token");  // 前端携带token访问后端
        boolean result = JwtUtils.checkToken(token);

        if (result) {
            return R.ok();
        } else {
            return R.setResult(ResponseEnum.LOGIN_AUTH_ERROR);
        }
    }

    @ApiOperation("校验手机号是否存在")
    @GetMapping("/checkMobile/{mobile}")
    public boolean checkMobileExist(@PathVariable String mobile) {
        return userInfoService.checkMobileExist(mobile);
    }
}

