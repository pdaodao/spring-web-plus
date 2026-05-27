package com.github.pdaodao.springwebplus.base.config;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.github.pdaodao.springwebplus.base.util.RequestUtil;
import org.apache.ibatis.reflection.MetaObject;
import java.time.LocalDateTime;

/**
 * 创建时间、更新时间
 */
public class DbFieldFillHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        final LocalDateTime now = LocalDateTimeUtil.now();
        this.setFieldValByName("createTime", now, metaObject);
        this.setFieldValByName("updateTime", now, metaObject);
        this.setFieldValByName("creatorId", RequestUtil.getUserId(), metaObject);
        this.setFieldValByName("creatorUsername", RequestUtil.getUsername(), metaObject);
        this.setFieldValByName("creatorNickname", RequestUtil.getUserNickname(), metaObject);
        this.setFieldValByName("updatorId", RequestUtil.getUserId(), metaObject);
        this.setFieldValByName("updatorUsername", RequestUtil.getUsername(), metaObject);
        this.setFieldValByName("updatorNickname", RequestUtil.getUserNickname(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        final LocalDateTime now = LocalDateTimeUtil.now();
        this.setFieldValByName("updateTime", now, metaObject);
        this.setFieldValByName("updatorId", RequestUtil.getUserId(), metaObject);
        this.setFieldValByName("updatorUsername", RequestUtil.getUsername(), metaObject);
        this.setFieldValByName("updatorNickname", RequestUtil.getUserNickname(), metaObject);
    }
}

