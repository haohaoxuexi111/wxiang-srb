package com.wxiang.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wxiang.srb.core.enums.BorrowerStatusEnum;
import com.wxiang.srb.core.enums.IntegralEnum;
import com.wxiang.srb.core.mapper.BorrowerAttachMapper;
import com.wxiang.srb.core.mapper.UserInfoMapper;
import com.wxiang.srb.core.mapper.UserIntegralMapper;
import com.wxiang.srb.core.pojo.entity.Borrower;
import com.wxiang.srb.core.mapper.BorrowerMapper;
import com.wxiang.srb.core.pojo.entity.BorrowerAttach;
import com.wxiang.srb.core.pojo.entity.UserInfo;
import com.wxiang.srb.core.pojo.entity.UserIntegral;
import com.wxiang.srb.core.pojo.vo.BorrowerApprovalVO;
import com.wxiang.srb.core.pojo.vo.BorrowerAttachVO;
import com.wxiang.srb.core.pojo.vo.BorrowerDetailVO;
import com.wxiang.srb.core.pojo.vo.BorrowerVO;
import com.wxiang.srb.core.service.BorrowerAttachService;
import com.wxiang.srb.core.service.BorrowerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxiang.srb.core.service.DictService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 借款人 服务实现类
 * </p>
 *
 * @author wxiang
 * @since 2022-04-20
 */
@Service
public class BorrowerServiceImpl extends ServiceImpl<BorrowerMapper, Borrower> implements BorrowerService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private BorrowerAttachMapper borrowerAttachMapper;

    @Resource
    private DictService dictService;

    @Resource
    private BorrowerAttachService borrowerAttachService;

    @Resource
    private UserIntegralMapper userIntegralMapper;

    @Override
    public void saveBorrowerVOByUserId(BorrowerVO borrowerVO, Long userId) {
        // 获取用户基本信息
        UserInfo userInfo = userInfoMapper.selectById(userId);

        // 保存借款人认证信息
        Borrower borrower = new Borrower();
        BeanUtils.copyProperties(borrowerVO, borrower);
        borrower.setUserId(userId);
        borrower.setName(userInfo.getName());
        borrower.setIdCard(userInfo.getIdCard());
        borrower.setMobile(userInfo.getMobile());
        borrower.setStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());
        baseMapper.insert(borrower);

        // 保存认证信息附件
        List<BorrowerAttach> borrowerAttachList = borrowerVO.getBorrowerAttachList();
        borrowerAttachList.forEach(borrowerAttach -> {
            borrowerAttach.setBorrowerId(borrower.getId());
            borrowerAttachMapper.insert(borrowerAttach);
        });

        // 更新userInfo中借款人的认证状态
        userInfo.setBorrowAuthStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());
        userInfoMapper.updateById(userInfo);
    }

    @Override
    public Integer getStatusByUserId(Long userId) {
        QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<>();
        borrowerQueryWrapper.select("status").eq("user_id", userId);
        List<Object> objects = baseMapper.selectObjs(borrowerQueryWrapper);  // 返回指定user_id的"status"字段
        if (objects.size() == 0) {
            return BorrowerStatusEnum.NO_AUTH.getStatus();  // 未认证
        }
        Integer status = (Integer) objects.get(0);
        return status;
    }

    // 借款人列表分页条件查询
    @Override
    public IPage<Borrower> listPage(Page<Borrower> pageParam, String keyword) {
        if (StringUtils.isBlank(keyword)) {
            return baseMapper.selectPage(pageParam, null);
        }
        QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<>();
        borrowerQueryWrapper
                .like("name", keyword)
                .or().like("id_card", keyword)
                .or().like("mobile", keyword)
                .orderByDesc("id");

        return baseMapper.selectPage(pageParam, borrowerQueryWrapper);
    }

    @Override
    public BorrowerDetailVO getBorrowerDetailVOById(Long id) {
        Borrower borrower = baseMapper.selectById(id);
        BorrowerDetailVO borrowerDetailVO = new BorrowerDetailVO();
        // 填充借款人基本信息，把能填充的自动填充了
        BeanUtils.copyProperties(borrower, borrowerDetailVO);

        // 婚否 在borrower中marry是Boolean类型，在borrowerDetailVO中是String类型
        borrowerDetailVO.setMarry(borrower.getMarry() ? "是" : "否");

        // 性别
        borrowerDetailVO.setSex(borrower.getSex() == 1 ? "男" : "女");

        // 下拉列表  根据dict表中的dict_code和value确定条目名称name
        borrowerDetailVO.setEducation(dictService.getNameByParentDictCodeAndValue("education", borrower.getEducation()));
        borrowerDetailVO.setIndustry(dictService.getNameByParentDictCodeAndValue("industry", borrower.getIndustry()));
        borrowerDetailVO.setIncome(dictService.getNameByParentDictCodeAndValue("income", borrower.getIncome()));
        borrowerDetailVO.setReturnSource(dictService.getNameByParentDictCodeAndValue("returnSource", borrower.getReturnSource()));
        borrowerDetailVO.setContactsRelation(dictService.getNameByParentDictCodeAndValue("relation", borrower.getContactsRelation()));

        // 审批状态
        String status = BorrowerStatusEnum.getMsgByStatus(borrower.getStatus());  // 根据枚举中的 status数字 返回数字对应的字符串 msg
        borrowerDetailVO.setStatus(status);

        // 附件列表
        List<BorrowerAttachVO> borrowerAttachVOList = borrowerAttachService.selectBorrowerAttachVOList(id);
        borrowerDetailVO.setBorrowerAttachVOList(borrowerAttachVOList);

        return borrowerDetailVO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void approval(BorrowerApprovalVO borrowerApprovalVO) {
        Long borrowerId = borrowerApprovalVO.getBorrowerId();
        Borrower borrower = baseMapper.selectById(borrowerId);
        borrower.setStatus(borrowerApprovalVO.getStatus());  // 修改认证状态
        baseMapper.updateById(borrower);
        Long userId = borrower.getUserId();
        int totalIntegral = 0;

        // 计算基本信息积分
        UserIntegral userIntegral = new UserIntegral();
        userIntegral.setUserId(userId);
        userIntegral.setIntegral(borrowerApprovalVO.getInfoIntegral());  // 基本信息积分
        userIntegral.setContent("借款人基本信息");
        userIntegralMapper.insert(userIntegral);
        totalIntegral += borrowerApprovalVO.getInfoIntegral();

        // 计算附件积分，并插入到user_integral表中
        // 身份证积分
        if (borrowerApprovalVO.getIsIdCardOk()) {
            userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_IDCARD.getIntegral());  // 基本信息积分
            userIntegral.setContent(IntegralEnum.BORROWER_IDCARD.getMsg());
            userIntegralMapper.insert(userIntegral);
            totalIntegral += IntegralEnum.BORROWER_IDCARD.getIntegral();
        }
        // 房产积分
        if (borrowerApprovalVO.getIsHouseOk()) {
            userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_HOUSE.getIntegral());  // 基本信息积分
            userIntegral.setContent(IntegralEnum.BORROWER_HOUSE.getMsg());
            userIntegralMapper.insert(userIntegral);
            totalIntegral += IntegralEnum.BORROWER_HOUSE.getIntegral();
        }
        // 车辆积分
        if (borrowerApprovalVO.getIsCarOk()) {
            userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_CAR.getIntegral());  // 基本信息积分
            userIntegral.setContent(IntegralEnum.BORROWER_CAR.getMsg());
            userIntegralMapper.insert(userIntegral);
            totalIntegral += IntegralEnum.BORROWER_CAR.getIntegral();
        }

        // 更新user_info表中的总积分字段 integral = 原积分 + 新增积分
        UserInfo userInfo = userInfoMapper.selectById(userId);
        Integer integral = userInfo.getIntegral();
        totalIntegral += integral;
        userInfo.setIntegral(totalIntegral);
        userInfoMapper.updateById(userInfo);

        // 修改审核状态
        userInfo.setBorrowAuthStatus(borrowerApprovalVO.getStatus());  // 认证审核通过就是 2，认证审核不通过的话就是 -1
        userInfoMapper.updateById(userInfo);

    }
}
