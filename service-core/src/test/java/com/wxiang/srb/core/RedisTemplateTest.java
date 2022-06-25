package com.wxiang.srb.core;

import com.wxiang.srb.core.mapper.DictMapper;
import com.wxiang.srb.core.pojo.entity.Dict;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/*
在SpringBoot项目中，默认集成Spring Data Redis，
Spring Data Redis针对Redis提供了非常方便的操作模板 Redis Template, 并且可以进行连接池自动管理
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTemplateTest {
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private DictMapper dictMapper;

    @Test
    public void saveData() {
        Dict dict = dictMapper.selectById(1);
        redisTemplate.opsForValue().set("dict", dict, 5, TimeUnit.MINUTES);  // dict的键和值都使用了jdk默认的序列化方案(存在安全隐患)，即实现接口Serializable
        Dict dict1 = (Dict) redisTemplate.opsForValue().get("dict");
        System.out.println(dict1.toString());
        // 使用了自定义的RedisConfig配置类之后保存到Redis中的序列化数据为
        // Dict(id=1, parentId=0, name=全部分类, value=null, dictCode=ROOT, createTime=2022-05-23T20:13:08, updateTime=2022-05-23T20:13:08, deleted=false, hasChildren=false)
    }
}
