package com.wxiang.srb.oss.service.impl;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.CannedAccessControlList;
import com.wxiang.common.exception.BusinessException;
import com.wxiang.common.result.ResponseEnum;
import com.wxiang.srb.oss.service.FileService;
import com.wxiang.srb.oss.util.OssProperties;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public String upload(InputStream inputStream, String module, String fileName) {
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        String endpoint = OssProperties.ENDPOINT;
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = OssProperties.KEY_ID;
        String accessKeySecret = OssProperties.KEY_SECRET;
        // 填写Bucket名称，例如examplebucket。
        String bucketName = OssProperties.BUCKET_NAME;

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 判断 BUCKET_NAME 是否存在
        if (!ossClient.doesBucketExist(bucketName)) {
            ossClient.createBucket(OssProperties.BUCKET_NAME);  // 不存在，则创建
            ossClient.setBucketAcl(OssProperties.BUCKET_NAME, CannedAccessControlList.PublicRead);
        }
        String key = "";
        try {
            // 文件目录结构定义成如 "module/2022/06/09/uuid.jpg" 的样子
            // 构建日期路径
            String timeFolder = new DateTime().toString("/yyyy/MM/dd/");
            // 文件名生成
            fileName = UUID.randomUUID().toString() + fileName.substring(fileName.lastIndexOf("."));  // .jpg

            key = module + timeFolder + fileName;
            // 上传文件流
            ossClient.putObject(bucketName, key, inputStream);
        } catch (OSSException oe) {
            oe.printStackTrace();  // 打印在控制台
            /*System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());*/
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR, oe);  // 返回前端，并打印在日志中
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            ossClient.shutdown();
        }
        // 返回上传文件的url地址
        return "https://" + OssProperties.BUCKET_NAME + "." + OssProperties.ENDPOINT + "/" + key;
    }

    @Override
    public void removeFile(String url) {

        // 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(
                OssProperties.ENDPOINT,
                OssProperties.KEY_ID,
                OssProperties.KEY_SECRET);

        // url 的格式如：https://oss-srb-space-1.oss-cn-shenzhen.aliyuncs.com/test/2022/06/09/bd84846b-c39a-467d-aa69-b2231adbedb5.jpeg
        String host = "https://" + OssProperties.BUCKET_NAME + "." + OssProperties.ENDPOINT + "/";  // url地址的前半部分
        String objectName = url.substring(host.length());  // objectName是从url截取的后半部分，即 test/2022/06/09/bd84846b-c39a-467d-aa69-b2231adbedb5.jpeg

        // 删除文件
        ossClient.deleteObject(OssProperties.BUCKET_NAME, objectName);

        // 关闭OSSClient
        ossClient.shutdown();
    }
}
