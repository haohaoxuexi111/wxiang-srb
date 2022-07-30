package com.wxiang.aliyun.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.AccessControlList;
import com.aliyun.oss.model.CannedAccessControlList;
import org.junit.Test;

public class OSSTest {

    // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
    String endpoint = "oss-cn-hangzhou.aliyuncs.com";
    // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
    String accessKeyId = "LTAI5tAt22S8aAAwWP3Vr7Fv";
    String accessKeySecret = "f7ekxayd3XFIak81TFWlgQgJfRukTs";

    @Test
    public void test() {


        // 填写Bucket名称
        String bucketName = "wxiang-srb-1";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 创建存储空间。
        ossClient.createBucket(bucketName);

        // 判断存储空间在阿里云平台上是否存在
        boolean exist = ossClient.doesBucketExist(bucketName);
        System.out.println(exist);  // true

        if (exist) {
            // 设置存储空间的访问权限为公共读
            ossClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
        }

        // 获取存储空间的访问权限
        AccessControlList acl = ossClient.getBucketAcl(bucketName);
        System.out.println(acl.toString());  // AccessControlList [owner=Owner [name=1519647698082143,id=1519647698082143], ACL=public-read]

        ossClient.shutdown();

    }

}
