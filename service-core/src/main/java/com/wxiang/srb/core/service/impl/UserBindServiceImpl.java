package com.wxiang.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wxiang.common.exception.Assert;
import com.wxiang.common.result.ResponseEnum;
import com.wxiang.srb.core.enums.UserBindEnum;
import com.wxiang.srb.core.hfb.FormHelper;
import com.wxiang.srb.core.hfb.HfbConst;
import com.wxiang.srb.core.hfb.RequestHelper;
import com.wxiang.srb.core.mapper.UserInfoMapper;
import com.wxiang.srb.core.pojo.entity.UserBind;
import com.wxiang.srb.core.mapper.UserBindMapper;
import com.wxiang.srb.core.pojo.entity.UserInfo;
import com.wxiang.srb.core.pojo.vo.UserBindVO;
import com.wxiang.srb.core.service.UserBindService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户绑定表 服务实现类
 * </p>
 *
 * @author wxiang
 * @since 2022-04-20
 */
@Service
public class UserBindServiceImpl extends ServiceImpl<UserBindMapper, UserBind> implements UserBindService {

    @Resource
    private UserInfoMapper userInfoMapper;

    // 根据用户提交的绑定表单，生成动态表单字符串
    @Override
    public String commitBindUser(UserBindVO userBindVO, Long userId) {

        // 若提交的绑定表单中的身份证与数据库中某记录的身份证相同，则不允许
        QueryWrapper<UserBind> userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper.eq("id_card", userBindVO.getIdCard())
                .ne("user_id", userId);
        UserBind userBind = baseMapper.selectOne(userBindQueryWrapper);
        Assert.isNull(userBind, ResponseEnum.USER_BIND_IDCARD_EXIST_ERROR);

        // 用户是否曾经填写过表单，但是没有完成全部的绑定流程
        userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper.eq("user_id", userId);
        userBind = baseMapper.selectOne(userBindQueryWrapper);

        if (userBind == null) {
            // 创建用户绑定记录
            userBind = new UserBind();
            BeanUtils.copyProperties(userBindVO, userBind);  // 拷贝源对象userBindVO的同名属性到目标对象userBind
            userBind.setUserId(userId);
            userBind.setStatus(UserBindEnum.NO_BIND.getStatus());
            baseMapper.insert(userBind);
        } else {
            // 若提交的表单在数据库中有相同的user_id，那么就取出数据，做更新
            BeanUtils.copyProperties(userBindVO, userBind);
            baseMapper.updateById(userBind);
        }

        // 自动提交的表单的参数设置
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentUserId", userId);
        paramMap.put("idCard", userBindVO.getIdCard());
        paramMap.put("personalName", userBindVO.getName());
        paramMap.put("bankType", userBindVO.getBankType());
        paramMap.put("bankNo", userBindVO.getBankNo());
        paramMap.put("mobile", userBindVO.getMobile());
        paramMap.put("returnUrl", HfbConst.USERBIND_RETURN_URL);
        paramMap.put("notifyUrl", HfbConst.USERBIND_NOTIFY_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        paramMap.put("sign", RequestHelper.getSign(paramMap));

        // 生成动态表单字符串，在这里使用了工具类FormHelper组装，并设置了自动提交表单
        /*String form = "<!DOCTYPE html>\n" +
                "<html lang=\"en\" xmlns:th=\"http://www.thymeleaf.org\">\n" +
                "  <head> </head>\n" +
                "  <body>\n" +
                "    <form name=\"form\" action=\"http://localhost:9999/userBind/BindAgreeUserV2\" method=\"post\">\n" +
                "    <!-- 参数 -->\n" +
                "    <input type=\"text\" name=\"mobile\" value=\""+ userBindVO.getMobile() +"\" />\n" +
                "    <input type=\"text\" name=\"sign\" value=\"\" />\n" +
                "    </form>\n" +
                "    <script>\n" +
                "      // 提交表单\n" +
                "      document.form.submit()\n" +
                "    </script>\n" +
                "  </body>\n" +
                "</html>";*/
        String formStr = FormHelper.buildForm(HfbConst.USERBIND_URL, paramMap);

        return formStr;
    }

    // hfb绑定回调，更新表user_bind和user_info
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void notify(Map<String, Object> paramMap) {
        String bindCode = (String) paramMap.get("bindCode");
        String userId = (String) paramMap.get("agentUserId");

        // 更新用户绑定表user_bind
        QueryWrapper<UserBind> userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper.eq("user_id", userId);
        UserBind userBind = baseMapper.selectOne(userBindQueryWrapper);
        userBind.setBindCode(bindCode);
        userBind.setStatus(UserBindEnum.BIND_OK.getStatus());
        baseMapper.updateById(userBind);

        // 更新用户表user_info
        UserInfo userInfo = userInfoMapper.selectById(userId);
        userInfo.setBindCode(bindCode);
        userInfo.setName(userBind.getName());
        userInfo.setIdCard(userBind.getIdCard());
        userInfo.setBindStatus(UserBindEnum.BIND_OK.getStatus());
        userInfoMapper.updateById(userInfo);
    }
}
