package com.wxiang.srb.core.service;

import com.wxiang.srb.core.pojo.dto.ExcelDictDTO;
import com.wxiang.srb.core.pojo.entity.Dict;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.InputStream;
import java.util.List;

/**
 * <p>
 * 数据字典 服务类
 * </p>
 *
 * @author wxiang
 * @since 2022-04-20
 */
public interface DictService extends IService<Dict> {
    // 接收前端上传的excel文件流，并写入到数据库中
    void importData(InputStream inputStream);

    List<ExcelDictDTO> listDictData();

    List<Dict> listByParentId(Long parentId);

    List<Dict> findByDictCode(String dictCode);

    String getNameByParentDictCodeAndValue(String dictCode, Integer value);
}
