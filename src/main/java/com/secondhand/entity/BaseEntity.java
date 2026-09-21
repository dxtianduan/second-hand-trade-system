package com.secondhand.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实体公共字段基类。
 * <p>
 * 15 张表里有 13 张都带 create_time / update_time，
 * 抽出来避免每张表的实体重复声明。
 *
 * @see com.secondhand.config.MetaObjectHandlerConfig 自动填充逻辑
 */
@Data
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 创建时间，插入时自动填充 */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间，插入和更新时都自动填充 */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
