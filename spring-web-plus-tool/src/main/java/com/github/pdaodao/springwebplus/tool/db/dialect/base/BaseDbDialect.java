package com.github.pdaodao.springwebplus.tool.db.dialect.base;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.table.DbInfo;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDialect;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbFunction;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbMetaLoader;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;

public abstract class BaseDbDialect implements DbDialect {

    @Override
    public DbMetaLoader metaLoader(DbInfo dbInfo) {
        return new JdbcMetaLoader(dbInfo, this);
    }

    protected String buildUrlDriverName(){
        return "mysql";
    }

    protected String buildUrlDriveSchemaName(){
        return null;
    }

    protected String buildUrlDefaultProperties(){
        return null;
    }


    @Override
    public String buildUrl(final DbInfo dbInfo) {
        Preconditions.checkNotBlank(dbInfo.getHost(), "主机地址不能为空");
        if(StrUtil.startWith(dbInfo.getUrl(), "jdbc:")){
            return dbInfo.getUrl();
        }
        String fmt = StrUtil.format("jdbc:{}://{}:{}/{}?", buildUrlDriverName(), dbInfo.getHost(), dbInfo.getPort(), dbInfo.getDbName());
        if(StrUtil.isNotBlank(buildUrlDriveSchemaName()) && StrUtil.isNotBlank(dbInfo.getDbSchema())){
            fmt += buildUrlDriveSchemaName()+"="+dbInfo.getDbSchema();
        }
        if(StrUtil.isBlank(dbInfo.getUrlProperties())){
            dbInfo.setUrlProperties(buildUrlDefaultProperties());
        }
        if(StrUtil.isNotBlank(dbInfo.getUrlProperties())){
            if(!StrUtil.endWith(fmt, "?")){
                fmt += "&";
            }
            fmt += dbInfo.getUrlProperties();
        }
        dbInfo.setUrl(fmt);
        return fmt;
    }

    @Override
    public DbFunction dbFunction() {
        return new BaseDbFunction();
    }
}
