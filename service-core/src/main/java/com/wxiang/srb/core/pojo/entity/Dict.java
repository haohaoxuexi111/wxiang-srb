package com.wxiang.srb.core.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 数据字典
 * </p>
 *
 * @author wxiang
 * @since 2022-04-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Dict对象", description="数据字典")
public class Dict implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "上级id")
    private Long parentId;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "值")
    private Integer value;

    @ApiModelProperty(value = "编码")
    private String dictCode;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;  // 代替了Date，LocalDateTime是线程安全的，只不过需要专门配置序列化方案

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "删除标记（0:不可用 1:可用）")
    @TableField("is_deleted")
    @TableLogic
    private Boolean deleted;

    // exist = false 表达逻辑概念的属性，与物理表没有关系，当前属性对应的字段不存在于物理表中
    @TableField(exist = false)
    private boolean hasChildren;  // element ui 以表格形式绚烂带子条目的数据字典需要用到hasChildren属性
}