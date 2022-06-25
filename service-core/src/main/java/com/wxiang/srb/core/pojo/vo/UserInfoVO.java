package com.wxiang.srb.core.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

// 在用户登录之后，封装用户基本信息，供前端使用
@Data
@ApiModel(description = "用户信息对象")
public class UserInfoVO {

    @ApiModelProperty(value = "用户对象")
    private String name;

    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    @ApiModelProperty(value = "头像")
    private String headImg;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "1：出借人  2：借款人")
    private Integer userType;

    @ApiModelProperty(value = "JWT访问令牌")
    private String token;

}
