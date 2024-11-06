package com.github.pdaodao.springwebplus.tool.db.core;

import lombok.Data;

import java.io.Serializable;

/**
 * 数据库连接信息
 */
@Data
public class DbInfo implements Serializable, Cloneable {
    /**
     * 编号
     */
    private String name;

    /**
     * 中文名称
     */
    private String title;

    /**
     * 备注
     */
    private String remark;

    /**
     * 数据库类型
     */
    private DbType dbType;

    /**
     * 主机地址
     */
    private String host;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 连接地址
     */
    private String url;

    /**
     * 驱动
     */
    private String driver;

    /**
     * 库名
     */
    private String dbName;

    /**
     * 库 schema
     */
    private String dbSchema;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 是否是只读
     */
    private Boolean readOnly;


    /**
     * 用作数据库连接池的key
     *
     * @return
     */
    public String key() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(url);
        sb.append(username);
        sb.append(password);
        sb.append(dbName);
        sb.append(getDbSchema());
        return sb.toString();
    }
}
