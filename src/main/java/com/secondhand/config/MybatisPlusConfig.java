package com.secondhand.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置。
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 分页插件。
     * <p>
     * 不注册这个的话，Page 对象的 total 永远是 0，
     * 前端分页组件会显示"共 0 条"，但列表数据却正常返回——很迷惑人。
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 分页插件：必须指定 DbType，否则不会自动识别数据库方言
        PaginationInnerInterceptor pagination = new PaginationInnerInterceptor(DbType.MYSQL);
        // 单页最大条数，防止前端传 size=999999 把库拖死
        pagination.setMaxLimit(100L);
        // 溢出总页数后是否进行处理（true = 超出最大页数时返回第一页）
        pagination.setOverflow(false);
        interceptor.addInnerInterceptor(pagination);

        // 防全表更新与删除：拦下没有 where 条件的 update/delete
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

        return interceptor;
    }
}
