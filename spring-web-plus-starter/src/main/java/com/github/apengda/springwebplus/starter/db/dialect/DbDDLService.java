package com.github.apengda.springwebplus.starter.db.dialect;

import com.github.apengda.springwebplus.starter.db.pojo.TableInfo;

import java.util.List;

public interface DbDDLService {

    DataTypeConverter getDataTypeConverter();


    /**
     * 建表语句
     * @param tableInfo
     * @return
     */
    List<String> createTable(final TableInfo tableInfo);

}
