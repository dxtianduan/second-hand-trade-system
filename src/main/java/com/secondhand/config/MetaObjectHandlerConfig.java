package com.secondhand.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 公共字段自动填充。
 * <p>
 * 实体上标注 {@code @TableField(fill = FieldFill.INSERT)} 的字段，
 * 在 insert 时自动填入当前时间，不用每个 Service 都手写 {@code setCreateTime(...)}。
 */
@Slf4j
@Component
public class MetaObjectHandlerConfig implements MetaObjectHandler {

    /** 插入时填充创建时间和更新时间 */
    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        // strictInsertFill 只在字段为 null 时才填充，避免覆盖业务主动设置的值
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
    }

    /** 更新时只填充更新时间 */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}
