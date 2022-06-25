package com.wxiang.srb.core.mapper;

import com.wxiang.srb.core.pojo.dto.ExcelDictDTO;
import com.wxiang.srb.core.pojo.entity.Dict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 数据字典 Mapper 接口
 * </p>
 *
 * @author wxiang
 * @since 2022-04-20
 */
public interface DictMapper extends BaseMapper<Dict> {

    void insertBatch(List<ExcelDictDTO> list);

}
