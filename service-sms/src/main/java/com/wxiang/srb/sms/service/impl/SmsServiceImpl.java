package com.wxiang.srb.sms.service.impl;

import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;
import com.wxiang.common.exception.Assert;
import com.wxiang.common.exception.BusinessException;
import com.wxiang.common.result.ResponseEnum;
import com.wxiang.srb.sms.service.SmsService;
import com.wxiang.srb.sms.util.SmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class SmsServiceImpl implements SmsService {
    @Override
    public void send(String mobile, String templateCode, Map<String, Object> param) {
        // 创建远程连接客户端对象
        /*DefaultProfile profile = DefaultProfile.getProfile(
                SmsProperties.REGION_ID,
                SmsProperties.KEY_ID,
                SmsProperties.KEY_SECRET);
        IAcsClient client = new DefaultAcsClient(profile);

        // 创建远程连接的各项请求参数
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysVersion("SendSms");
        request.putQueryParameter("RegionId", SmsProperties.REGION_ID);
        request.putQueryParameter("PhoneNumbers", mobile);
        request.putQueryParameter("SignName", SmsProperties.SIGN_NAME);
        request.putQueryParameter("TemplateCode", templateCode);

        Gson gson = new Gson();
        String jsonParam = gson.toJson(param);  // 将生成的参数转为json字符串，再传给阿里云服务器
        request.putQueryParameter("TemplateParam", jsonParam);

        try {
            // 使用客户端对象携带请求参数，向远程阿里云服务器发起远程调用，并得到响应
            CommonResponse response = client.getCommonResponse(request);
            System.out.println("阿里云sms服务的响应信息：" + response.getData());

            // 通信失败
            boolean success = response.getHttpResponse().isSuccess();
            Assert.isTrue(success, ResponseEnum.ALIYUN_RESPONSE_ERROR);

            // 获取响应结果
            String data = response.getData();
            HashMap<String, String> resultMap = gson.fromJson(data, HashMap.class);
            String code = resultMap.get("Code");
            String message = resultMap.get("Message");
            log.info("code: " + code + ", message: " + message);

            // 业务处理失败
            Assert.notEquals("isv.BUSINESS_LIMIT_CONTROL", message, ResponseEnum.ALIYUN_SMS_LIMIT_CONTROL_ERROR);  // 抛出短信发送过于频繁异常
            Assert.equals("OK", code, ResponseEnum.ALIYUN_SMS_ERROR);  // Code不等于OK时抛出ALIYUN_SMS_ERROR异常
        } catch (ServerException e) {
            log.error("调用阿里云短信发送sdk失败: " + e.getErrCode() + ", " + e.getErrMsg()); // 记录到日志中
            throw new BusinessException(ResponseEnum.ALIYUN_SMS_ERROR, e);  // 返回给前端的错误信息，同时将错误跟踪栈的信息记录到日志
            // e.printStackTrace();  // 错误跟踪栈信息只会将错误信息打印在控制台，不会打印到日志中
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());

            log.error("调用阿里云短信发送sdk失败: " + e.getErrCode() + ", " + e.getErrMsg());
            throw new BusinessException(ResponseEnum.ALIYUN_SMS_ERROR, e);
        }
*/

        DefaultProfile profile = DefaultProfile.getProfile(
                SmsProperties.REGION_ID,
                SmsProperties.KEY_ID,
                SmsProperties.KEY_SECRET);
        IAcsClient client = new DefaultAcsClient(profile);
        SendSmsRequest request = new SendSmsRequest();
        try {
            SendSmsResponse response = client.getAcsResponse(request);
            Gson gson = new Gson();
            String res = gson.toJson(response);
            System.out.println("阿里云sms服务的响应信息：" + res);

            // 获取响应结果
            String data = res;
            HashMap<String, String> resultMap = gson.fromJson(data, HashMap.class);  // 将json字符串转为map格式
            String code = resultMap.get("Code");
            String message = resultMap.get("Message");
            // 业务处理失败
            Assert.notEquals("isv.BUSINESS_LIMIT_CONTROL", message, ResponseEnum.ALIYUN_SMS_LIMIT_CONTROL_ERROR);  // 抛出短信发送过于频繁异常
            Assert.equals("OK", code, ResponseEnum.ALIYUN_SMS_ERROR);  // Code不等于OK时抛出ALIYUN_SMS_ERROR异常
        } catch (ServerException e) {
            log.error("调用阿里云短信发送sdk失败: " + e.getErrCode() + ", " + e.getErrMsg()); // 记录到日志中
            throw new BusinessException(ResponseEnum.ALIYUN_SMS_ERROR, e);  // 返回给前端的错误信息，同时将错误跟踪栈的信息记录到日志
            // e.printStackTrace();  // 错误跟踪栈信息只会将错误信息打印在控制台，不会打印到日志中
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());

            log.error("调用阿里云短信发送sdk失败: " + e.getErrCode() + ", " + e.getErrMsg());
            throw new BusinessException(ResponseEnum.ALIYUN_SMS_ERROR, e);
        }
    }



}
