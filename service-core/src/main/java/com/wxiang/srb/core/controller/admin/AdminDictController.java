package com.wxiang.srb.core.controller.admin;


import com.alibaba.excel.EasyExcel;
import com.wxiang.common.exception.BusinessException;
import com.wxiang.common.result.R;
import com.wxiang.common.result.ResponseEnum;
import com.wxiang.srb.core.pojo.dto.ExcelDictDTO;
import com.wxiang.srb.core.pojo.entity.Dict;
import com.wxiang.srb.core.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * <p>
 * 数据字典 前端控制器
 * </p>
 *
 * @author wxiang
 * @since 2022-04-20
 */
@Api(tags = "数据字典管理")
@RestController
@RequestMapping("/admin/core/dict")
@Slf4j
// @CrossOrigin // 这是用来解决nginx跨域问题的，gateway不需要，且必须要注释掉
public class AdminDictController {

    @Resource
    private DictService dictService;

    // 将用户从页面上传的excel文件导入到数据库中
    @ApiOperation("Excel数据的批量导入")
    @PostMapping("/import")
    public R batchImport(
            @ApiParam(value = "Excel数据字典文件", required = true)
            @RequestParam("file")MultipartFile file) {
        try {
            dictService.importData(file.getInputStream());
            return R.ok().message("数据字典批量导入成功");
        } catch (IOException e) {
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR, e);
        }
    }

    @ApiOperation("Excel数据导出")
    @GetMapping("/export")
    public void download(HttpServletResponse response) throws IOException {
        // 这里注意 使用swagger 可能会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("dict", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), ExcelDictDTO.class).sheet("数据字典").doWrite(dictService.listDictData()); // listDictData() 获取要操作的dto对象列表
    }

    // 根据数据字典的父类别id，获取子条目
    @ApiOperation("根据上级id获取子节点数据列表")
    @GetMapping("/listByParentId/{parentId}")
    public R listByParentId(@ApiParam(value = "上级节点id", required = true)
                            @PathVariable Long parentId) {
        List<Dict> dictList = dictService.listByParentId(parentId);
        return R.ok().data("list", dictList);
    }
}

