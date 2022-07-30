package com.wxiang.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wxiang.common.exception.Assert;
import com.wxiang.common.result.ResponseEnum;
import com.wxiang.srb.core.enums.BorrowAuthEnum;
import com.wxiang.srb.core.enums.BorrowInfoStatusEnum;
import com.wxiang.srb.core.enums.BorrowerStatusEnum;
import com.wxiang.srb.core.enums.UserBindEnum;
import com.wxiang.srb.core.mapper.IntegralGradeMapper;
import com.wxiang.srb.core.mapper.UserInfoMapper;
import com.wxiang.srb.core.pojo.entity.BorrowInfo;
import com.wxiang.srb.core.mapper.BorrowInfoMapper;
import com.wxiang.srb.core.pojo.entity.IntegralGrade;
import com.wxiang.srb.core.pojo.entity.UserInfo;
import com.wxiang.srb.core.service.BorrowInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 借款信息表 服务实现类
 * </p>
 *
 * @author wxiang
 * @since 2022-04-20
 */
@Service
public class BorrowInfoServiceImpl extends ServiceImpl<BorrowInfoMapper, BorrowInfo> implements BorrowInfoService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private IntegralGradeMapper integralGradeMapper;

    @Override
    public BigDecimal getBorrowAmount(Long userId) {
        // 获取用户积分
        UserInfo userInfo = userInfoMapper.selectById(userId);
        Assert.notNull(userInfo, ResponseEnum.LOGIN_MOBILE_ERROR);
        Integer integral = userInfo.getIntegral();

        // 根据积分查询额度
        QueryWrapper<IntegralGrade> integralGradeQueryWrapper = new QueryWrapper<>();
        integralGradeQueryWrapper
                .le("integral_start", integral)  // integral_start <= integral
                .ge("integral_end", integral);   // integral_end >= integral
        IntegralGrade integralGrade = integralGradeMapper.selectOne(integralGradeQueryWrapper);
        if (integralGrade == null) {
            return new BigDecimal("0");
        }
        return integralGrade.getBorrowAmount();
    }

    @Override
    public void saveBorrowInfo(BorrowInfo borrowInfo, Long userId) {

        UserInfo userInfo = userInfoMapper.selectById(userId);

        // 判断用户绑定状态
        Assert.isTrue(
                userInfo.getBindStatus().intValue() == UserBindEnum.BIND_OK.getStatus().intValue(),
                ResponseEnum.USER_NO_BIND_ERROR
        );

        // 判断借款人额度审批状态
        Assert.isTrue(
                userInfo.getBorrowAuthStatus().intValue() == BorrowerStatusEnum.AUTH_OK.getStatus().intValue(),
                ResponseEnum.USER_NO_AMOUNT_ERROR
        );

        // 判断借款人额度是否充足
        BigDecimal borrowAmount = this.getBorrowAmount(userId);
        Assert.isTrue(
                borrowInfo.getAmount().doubleValue() <= borrowAmount.doubleValue(),
                ResponseEnum.USER_AMOUNT_LESS_ERROR
        );

        // 存储borrowInfo
        borrowInfo.setUserId(userId);
        borrowInfo.setBorrowYearRate(borrowInfo.getBorrowYearRate().divide(new BigDecimal(100)));  // 利率百分比转小数
        borrowInfo.setStatus(BorrowInfoStatusEnum.CHECK_RUN.getStatus());  // 设置借款申请的审核状态
        baseMapper.insert(borrowInfo);
    }

    @Override
    public Integer getStatusByUserId(Long userId) {
        QueryWrapper<BorrowInfo> borrowInfoQueryWrapper = new QueryWrapper<>();
        // userInfoQueryWrapper.eq("user_id", userId);
        // BorrowInfo borrowInfo = baseMapper.selectOne(userInfoQueryWrapper);

        borrowInfoQueryWrapper.select("status").eq("user_id", userId);  // 查询指定userId的status字段的数据
        List<Object> objects = baseMapper.selectObjs(borrowInfoQueryWrapper);
        if (objects.size() == 0) {
            return BorrowInfoStatusEnum.NO_AUTH.getStatus();  // 0未认证；1审核中；2审核通过；-1审核未通过
        }
        Integer status = (Integer) objects.get(0);
        return status;
    }
}
