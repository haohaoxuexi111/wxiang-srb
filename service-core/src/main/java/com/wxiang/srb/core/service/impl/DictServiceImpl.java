package com.wxiang.srb.core.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wxiang.srb.core.listener.ExcelDictDTOListener;
import com.wxiang.srb.core.pojo.dto.ExcelDictDTO;
import com.wxiang.srb.core.pojo.entity.Dict;
import com.wxiang.srb.core.mapper.DictMapper;
import com.wxiang.srb.core.service.DictService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author wxiang
 * @since 2022-04-20
 */
@Slf4j
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {
    /*
    DictMapper 接口声明： public interface DictMapper extends BaseMapper<Dict>
    DictServiceImpl 类继承了 ServiceImpl<DictMapper, Dict>，而ServiceImpl类中注入了成员变量DictMapper baseMapper：
    public class ServiceImpl<M extends BaseMapper<T>, T> implements IService<T> {
        @Autowired
        protected M baseMapper;
    所以这里的baseMapper可以拿来即用，不用再注入。
     */
    /*@Resource  // 如果要注入的mapper就是当前service下的mapper的话，可以省略
    private DictMapper dictMapper;*/

    @Transactional(rollbackFor = {Exception.class})  // 写入数据库出现异常时，回滚
    @Override
    public void importData(InputStream inputStream) {
        // 读取上传的excel输入流
        EasyExcel.read(inputStream, ExcelDictDTO.class, new ExcelDictDTOListener(baseMapper)).sheet().doRead();
        log.info("Excel导入成功!!");
    }


    @Override
    public List<ExcelDictDTO> listDictData() {
        List<Dict> dictList = baseMapper.selectList(null);
        // ExcelDictDTO 是easyexcel操作Excel使用的dto对象，而Dict则是操作数据库中dict表使用的实体类对象，需要做转换
        // 从数据库中读取dict表格内容，并封装到Dict列表对象中，接着，创建ExcelDictDTO列表，将Dict列表转换成ExcelDictDTO列表
        ArrayList<ExcelDictDTO> excelDictDTOList = new ArrayList<>(dictList.size());
        dictList.forEach(dict -> {
            ExcelDictDTO excelDictDTO = new ExcelDictDTO();
            BeanUtils.copyProperties(dict, excelDictDTO);  // 对象拷贝
            excelDictDTOList.add(excelDictDTO);
        });
        return excelDictDTOList;
    }

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public List<Dict> listByParentId(Long parentId) {
        try {
            // 查询redis中是否存在数据列表，而不是每次都从数据库中拿数据
            List<Dict> dictList = (List<Dict>) redisTemplate.opsForValue().get("srb:core:dictList:" + parentId);  // 将parentId相同的数据存到一个key中
            if (dictList != null) {
                // 如果redis中存在dict数据列表，则从redis中直接返回数据列表
                log.info("从Redis中获取的数据列表");
                return dictList;
            }
        } catch (Exception e) {
            log.info("redis服务器取数据异常：" + ExceptionUtils.getStackTrace(e));  // 出错了就打印错误跟踪栈的信息，并从数据库中查询数据
        }

        // redis中不存在才从数据库中拿数据
        log.info("从数据库中查询数据列表");
        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("parent_id", parentId);
        List<Dict> dictList = baseMapper.selectList(dictQueryWrapper);
        // 填充hasChildren字段，判断是否有dict的parent_id是当前dict的id
        dictList.forEach(dict -> {
            // 根据hasChildren属性判断当前节点是否有子节点，找到当前的dict下级有没有子节点
            boolean hasChildren = this.hasChildren(dict.getId());
            dict.setHasChildren(hasChildren);
        });

        try {
            // 从数据库中查询到dict列表数据之后，将数据存入redis中
            log.info("将数据库中查询到的数据存入Redis");
            redisTemplate.opsForValue().set("srb:core:dictList:" + parentId, dictList,5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.info("redis服务器存数据异常：" + ExceptionUtils.getStackTrace(e));
        }

        return dictList;
    }

    // 判断当前id所在的节点下(类别)是否有子节点(条目)
    private boolean hasChildren(Long id) {
        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("parent_id", id); // 判断是否有dict的parent_id是当前dict的id
        Integer count = baseMapper.selectCount(dictQueryWrapper);
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }
}
