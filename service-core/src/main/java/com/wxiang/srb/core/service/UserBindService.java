package com.wxiang.srb.core.service;

import com.wxiang.srb.core.pojo.entity.UserBind;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wxiang.srb.core.pojo.vo.UserBindVO;

import java.util.Map;

/**
 * <p>
 * 用户绑定表 服务类
 * </p>
 *
 * @author wxiang
 * @since 2022-04-20
 */
public interface UserBindService extends IService<UserBind> {

    String commitBindUser(UserBindVO userBindVO, Long userId);

    void notify(Map<String, Object> paramMap);
}
