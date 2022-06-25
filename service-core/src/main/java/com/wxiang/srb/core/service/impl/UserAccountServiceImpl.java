package com.wxiang.srb.core.service.impl;

import com.wxiang.srb.core.pojo.entity.UserAccount;
import com.wxiang.srb.core.mapper.UserAccountMapper;
import com.wxiang.srb.core.service.UserAccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户账户 服务实现类
 * </p>
 *
 * @author wxiang
 * @since 2022-04-20
 */
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {

}
