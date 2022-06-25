package com.wxiang.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wxiang.common.exception.Assert;
import com.wxiang.common.result.ResponseEnum;
import com.wxiang.common.util.MD5;
import com.wxiang.srb.base.util.JwtUtils;
import com.wxiang.srb.core.mapper.UserAccountMapper;
import com.wxiang.srb.core.mapper.UserLoginRecordMapper;
import com.wxiang.srb.core.pojo.entity.UserAccount;
import com.wxiang.srb.core.pojo.entity.UserInfo;
import com.wxiang.srb.core.mapper.UserInfoMapper;
import com.wxiang.srb.core.pojo.entity.UserLoginRecord;
import com.wxiang.srb.core.pojo.query.UserInfoQuery;
import com.wxiang.srb.core.pojo.vo.LoginVO;
import com.wxiang.srb.core.pojo.vo.RegisterVO;
import com.wxiang.srb.core.pojo.vo.UserInfoVO;
import com.wxiang.srb.core.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 用户基本信息 服务实现类
 * </p>
 *
 * @author wxiang
 * @since 2022-04-20
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Resource
    private UserAccountMapper userAccountMapper;

    @Resource
    private UserLoginRecordMapper userLoginRecordMapper;

    /**
     * 用户注册
     * @param registerVO
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void register(RegisterVO registerVO) {
        // 判断用户手机号是否已经被注册
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile", registerVO.getMobile());
        //    @Autowired  由于本类继承了ServiceImpl，而ServiceImpl已经注入了baseMapper，所以这里可以直接使用
        //    protected M baseMapper;
        Integer count = baseMapper.selectCount(wrapper);
        Assert.isTrue(count == 0, ResponseEnum.MOBILE_EXIST_ERROR);

        // 插入用户信息 user_info
        UserInfo userInfo = new UserInfo();
        userInfo.setUserType(registerVO.getUserType());
        userInfo.setNickName(registerVO.getMobile());
        userInfo.setName(registerVO.getMobile());
        userInfo.setMobile(registerVO.getMobile());
        userInfo.setPassword(MD5.encrypt(registerVO.getPassword()));
        userInfo.setStatus(UserInfo.STATUS_NORMAL);
        userInfo.setHeadImg(UserInfo.USER_AVATAR);
        baseMapper.insert(userInfo);

        // 插入用户账户记录
        UserAccount userAccount = new UserAccount();
        userAccount.setUserId(userInfo.getId());
        userAccountMapper.insert(userAccount);
    }

    /**
     * 用户登录
     * @param loginVO
     * @param ip
     * @return
     */
    @Transactional(rollbackFor = Exception.class)  // 相当于TransactionDefinition
    @Override
    public UserInfoVO login(LoginVO loginVO, String ip) {

        String mobile = loginVO.getMobile();
        String password = loginVO.getPassword();
        Integer userType = loginVO.getUserType();

        // 判断用户是否存在
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper
                .eq("mobile", mobile)
                .eq("user_type", userType);
        UserInfo userInfo = baseMapper.selectOne(userInfoQueryWrapper);
        Assert.notNull(userInfo, ResponseEnum.LOGIN_MOBILE_ERROR);

        // 密码是否正确
        Assert.equals(MD5.encrypt(password), userInfo.getPassword(), ResponseEnum.LOGIN_PASSWORD_ERROR);

        // 用户数是否被禁用
        Assert.equals(userInfo.getStatus(), UserInfo.STATUS_NORMAL, ResponseEnum.LOGIN_LOKED_ERROR);

        // 记录登录日志
        UserLoginRecord userLoginRecord = new UserLoginRecord();
        userLoginRecord.setUserId(userInfo.getId());
        userLoginRecord.setIp(ip);
        userLoginRecordMapper.insert(userLoginRecord);

        // 生成token
        String token = JwtUtils.createToken(userInfo.getId(), userInfo.getName());
        System.out.println("token：" + token);

        // 组装UserInfoVO， userInfoVO主要封装登录用户的基本信息，在controller中返回给前端使用
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setToken(token);
        userInfoVO.setName(userInfo.getName());
        userInfoVO.setNickName(userInfo.getNickName());
        userInfoVO.setHeadImg(userInfo.getHeadImg());
        userInfoVO.setMobile(mobile);
        userInfoVO.setUserType(userType);

        return userInfoVO;
    }


    /**
     * 根据查询条件对象 userInfoQuery 和 分页参数pageParam 分页查询 userInfo 类型的记录
     * @param pageParam
     * @param userInfoQuery
     * @return
     */
    @Override
    public IPage<UserInfo> listPage(Page<UserInfo> pageParam, UserInfoQuery userInfoQuery) {
        if (userInfoQuery == null) {
            return baseMapper.selectPage(pageParam, null);
        }

        String mobile = userInfoQuery.getMobile();
        Integer userType = userInfoQuery.getUserType();
        Integer status = userInfoQuery.getStatus();

        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper
                .eq(StringUtils.isNotBlank(mobile), "mobile", mobile)
                .eq(status != null, "status", status)   // condition为true，则组装后面的查询条件
                .eq(userType != null, "user_type", userType);


        return baseMapper.selectPage(pageParam, userInfoQueryWrapper);
    }

    @Override
    public void lock(Long id, Integer status) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setStatus(status);
        baseMapper.updateById(userInfo);
    }

    @Override
    public boolean checkMobileExist(String mobile) {
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("mobile", mobile);
        Integer count = baseMapper.selectCount(userInfoQueryWrapper);
        return count > 0;
    }

}
