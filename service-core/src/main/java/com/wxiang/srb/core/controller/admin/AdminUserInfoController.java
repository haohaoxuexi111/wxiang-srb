package com.wxiang.srb.core.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wxiang.common.exception.Assert;
import com.wxiang.common.result.R;
import com.wxiang.common.result.ResponseEnum;
import com.wxiang.common.util.RegexValidateUtils;
import com.wxiang.srb.base.util.JwtUtils;
import com.wxiang.srb.core.pojo.entity.UserInfo;
import com.wxiang.srb.core.pojo.query.UserInfoQuery;
import com.wxiang.srb.core.pojo.vo.LoginVO;
import com.wxiang.srb.core.pojo.vo.RegisterVO;
import com.wxiang.srb.core.pojo.vo.UserInfoVO;
import com.wxiang.srb.core.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@RequestMapping("/admin/core/userInfo")
@Slf4j
//@CrossOrigin  // 这是用来解决nginx跨域问题的，gateway不需要，且必须要注释掉
public class AdminUserInfoController {

    @Resource
    private UserInfoService userInfoService;

    @ApiOperation(("获取会员(条件)分页列表"))
    @GetMapping("/list/{page}/{limit}")  // @RequestBody只能在有请求体的请求中使用
    public R listPage(
            @ApiParam(value = "当前页码", required = true)
            @PathVariable("page") Long page,
            @ApiParam(value = "每页记录数", required = true)
            @PathVariable("limit") Long limit,
            @ApiParam(value = "分页查询对象", required = false)
            UserInfoQuery userInfoQuery
    ) {
        Page<UserInfo> pageParam = new Page<>(page, limit);
        IPage<UserInfo> pageModel = userInfoService.listPage(pageParam, userInfoQuery);
        return R.ok().data("pageModel", pageModel);
    }

    @ApiOperation("锁定和解锁")
    @PutMapping("/lock/{id}/{status}")
    public R lock(
            @ApiParam(value = "用户id", required = true)
            @PathVariable("id") Long id,
            @ApiParam(value = "锁定状态", required = true)
            @PathVariable("status") Integer status) {
        userInfoService.lock(id, status);
        return R.ok().message(status == 1 ? "解锁成功" : "锁定成功");  // status=1是正常状态；status=0是锁定状态
    }
}

