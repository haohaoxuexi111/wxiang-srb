package com.wxiang.srb.core.controller.admin;


import com.wxiang.common.result.R;
import com.wxiang.srb.core.pojo.entity.UserLoginRecord;
import com.wxiang.srb.core.service.UserLoginRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 用户登录记录表 前端控制器
 * </p>
 *
 * @author wxiang
 * @since 2022-04-20
 */
@Api(tags = "会员登录日志")
@Slf4j
//@CrossOrigin  // 这是用来解决nginx跨域问题的，gateway不需要，且必须要注释掉
@RestController
@RequestMapping("admin/core/userLoginRecord")
public class AdminUserLoginRecordController {

    @Resource
    private UserLoginRecordService userLoginRecordService;

    @ApiOperation("获取会员前50条登录日志")
    @GetMapping("/listTop50/{userId}")
    public R listTop50(
            @ApiParam(value = "用户id", required = true)
            @PathVariable Long userId
    ) {
        List<UserLoginRecord> userLoginRecordList = userLoginRecordService.listTop50(userId);
        return R.ok().data("list", userLoginRecordList);
    }

}

