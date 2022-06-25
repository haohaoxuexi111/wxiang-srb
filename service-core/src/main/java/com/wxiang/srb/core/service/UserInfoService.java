package com.wxiang.srb.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wxiang.srb.core.pojo.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wxiang.srb.core.pojo.query.UserInfoQuery;
import com.wxiang.srb.core.pojo.vo.LoginVO;
import com.wxiang.srb.core.pojo.vo.RegisterVO;
import com.wxiang.srb.core.pojo.vo.UserInfoVO;

/**
 * <p>
 * 用户基本信息 服务类
 * </p>
 *
 * @author wxiang
 * @since 2022-04-20
 */
public interface UserInfoService extends IService<UserInfo> {

    UserInfoVO login(LoginVO loginVO, String ip);

    void register(RegisterVO registerVO);

    IPage<UserInfo> listPage(Page<UserInfo> pageParam, UserInfoQuery userInfoQuery);

    void lock(Long id, Integer status);

    boolean checkMobileExist(String mobile);
}
