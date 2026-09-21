package com.secondhand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondhand.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据访问层。
 * <p>
 * 继承 {@link BaseMapper} 就已具备单表增删改查能力，
 * 无需写 XML，也无需在启动类上加 {@code @MapperScan}（这里用了 {@code @Mapper} 逐个标注）。
 * <p>
 * 注意：MyBatis-Plus 的 {@code selectById} 等方法是<b>物理删除</b>语义，
 * 但本项目用 {@code status} 做逻辑控制（封禁而非删除），所以不要写"删用户"的接口。
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
