package com.wxiang.srb.core;

import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

public class AssertTests {

    @Test
    public void test1(){
        Object obj = null;
        if (obj == null){
            throw new IllegalArgumentException("参数为null");
        }
    }

    @Test
    public void test2(){
        Object obj = null;
        // 使用断言代替if结构
        Assert.notNull(obj, "参数错误");
    }
}
