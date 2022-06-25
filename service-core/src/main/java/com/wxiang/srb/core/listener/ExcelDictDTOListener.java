package com.wxiang.srb.core.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.wxiang.srb.core.mapper.DictMapper;
import com.wxiang.srb.core.pojo.dto.ExcelDictDTO;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j  // 这个类不能被spring管理
@NoArgsConstructor
public class ExcelDictDTOListener extends AnalysisEventListener<ExcelDictDTO> {

    private DictMapper dictMapper;

    public ExcelDictDTOListener(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
    }

    // 数据列表
    List<ExcelDictDTO> list = new ArrayList<ExcelDictDTO>();
    // 每当列表中满 BATCH_COUNT 条数据时，就批量写入数据库一次
    static final int BATCH_COUNT = 5;

    @Override  // 监听器封装每行数据为ExcelDictDTO对象，excel每有一行数据，这个方法就会执行一次
    public void invoke(ExcelDictDTO data, AnalysisContext analysisContext) {
        log.info("解析到一条记录: {}", data);
        // 为了减少执行sql的次数，将数据累计到列表中达到一定数量时，再一次性写入数据库
        list.add(data);
        if (list.size() >= BATCH_COUNT) {
            // 调用mapper层的save方法
            saveData();
            list.clear();
        }

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // 数据列表list中的数据条数达不到 BATCH_COUNT 条时，在这里一次性存储剩余数据
        saveData();
        log.info("所有数据解析完成!!");
    }

    private void saveData() {
        log.info("{} 条数据被存储到数据库中", list.size());
        // 调用mapper层的save方法，批量保存list中的对象
        dictMapper.insertBatch(list);

        log.info("{} 条数据存储成功", list.size());
    }
}
