package com.wxiang.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wxiang.srb.core.pojo.entity.UserLoginRecord;
import com.wxiang.srb.core.mapper.UserLoginRecordMapper;
import com.wxiang.srb.core.service.UserLoginRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户登录记录表 服务实现类
 * </p>
 *
 * @author wxiang
 * @since 2022-04-20
 */
@Service
public class UserLoginRecordServiceImpl extends ServiceImpl<UserLoginRecordMapper, UserLoginRecord> implements UserLoginRecordService {

    @Override
    public List<UserLoginRecord> listTop50(Long userId) {
        QueryWrapper<UserLoginRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).orderByDesc("id").last("limit 50");  // last表示在sql语句后面追加代码片段
        List<UserLoginRecord> userLoginRecordList = baseMapper.selectList(wrapper);
        return userLoginRecordList;
    }
}
