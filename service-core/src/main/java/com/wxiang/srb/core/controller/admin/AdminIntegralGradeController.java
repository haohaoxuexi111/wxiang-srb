package com.wxiang.srb.core.controller.admin;


import com.wxiang.common.exception.Assert;
import com.wxiang.common.exception.BusinessException;
import com.wxiang.common.result.R;
import com.wxiang.common.result.ResponseEnum;
import com.wxiang.srb.core.pojo.entity.IntegralGrade;
import com.wxiang.srb.core.service.IntegralGradeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 积分等级表 前端控制器
 * </p>
 *
 * @author wxiang
 * @since 2022-04-20
 */
@Api(tags = "积分等级管理")
@Slf4j
//@CrossOrigin  // 这是用来解决nginx跨域问题的，gateway不需要，且必须要注释掉
@RestController
@RequestMapping("/admin/core/integralGrade")
public class AdminIntegralGradeController {

    @Resource
    private IntegralGradeService integralGradeService;

    @ApiOperation("获取积分等级列表")
    @GetMapping("/list")
    public R listAll(){

        log.info("hello, this is info level");
        log.warn("hello, this is warn level");
        log.error("hello, this is error level");

        List<IntegralGrade> list = integralGradeService.list();
        return R.ok().data("list", list).message("获取积分等级列表成功");
    }

    @ApiOperation(value = "根据id删除数据记录", notes = "逻辑删除")
    @DeleteMapping("/remove/{id}")
    public R removeById(
            @ApiParam(value = "数据id", example = "100", required = true)
            @PathVariable Long id){

        boolean result = integralGradeService.removeById(id);
        if (result){
            return R.ok().message("删除积分等级记录成功");
        }else {
            return R.error().message("删除积分等级记录失败");
        }
    }

    @ApiOperation("新增积分等级")
    @PostMapping("/save")
    public R save(
            @ApiParam(value = "积分等级对象", required = true)
            @RequestBody IntegralGrade integralGrade){

        /*if (integralGrade.getBorrowAmount() == null){
            throw new BusinessException(ResponseEnum.BORROW_AMOUNT_NULL_ERROR);  // 抛出自定义异常，对于其它可能出现的各种异常只需要使用不同的枚举对象即可
        }*/
        Assert.notNull(integralGrade.getBorrowAmount(), ResponseEnum.BORROW_AMOUNT_NULL_ERROR);

        boolean result = integralGradeService.save(integralGrade);
        if (result){
            return R.ok().message("新增成功");
        }else {
            return R.error().message("新增失败");
        }
    }

    @ApiOperation("根据id查询积分等级记录")
    @GetMapping("/getById/{id}")
    public R getById(
            @ApiParam(value = "积分等级的id", required = true, example = "1")
            @PathVariable Integer id){
        IntegralGrade integralGrade = integralGradeService.getById(id);
        if (integralGrade != null){
            return R.ok().data("record", integralGrade);
        }else {
            return R.error().message("数据查询失败");
        }
    }

    @ApiOperation("更新积分等级")
    @PutMapping("/update")
    public R updateById(
            @ApiParam(value = "积分等级对象", required = true)
            @RequestBody IntegralGrade integralGrade){

        boolean result = integralGradeService.updateById(integralGrade);
        if (result){
            return R.ok().message("更新成功");
        }else {
            return R.error().message("更新失败");
        }
    }
}

