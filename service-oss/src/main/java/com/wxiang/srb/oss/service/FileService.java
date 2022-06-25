package com.wxiang.srb.oss.service;

import java.io.InputStream;

public interface FileService {
    /**
     * 文件上传至阿里云
     */
    String upload(InputStream inputStream, String module, String fileName);

    /**
     * 删除oss中指定url的文件
     * @param url
     */
    void removeFile(String url);
}
