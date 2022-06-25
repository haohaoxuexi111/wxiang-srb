package com.wxiang.srb.core.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

// 页面内容、表单展示的数据可以定义成一个VO(value object)
@Data
@ApiModel(description = "注册对象")
public class RegisterVO {

    @ApiModelProperty(value = "用户类型")
    private Integer userType;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "验证码")
    private String code;

    @ApiModelProperty(value = "密码")
    private String password;
}
