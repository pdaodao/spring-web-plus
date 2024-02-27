package com.github.apengda.springwebplus.starter.db.dialect.mysql;

import com.github.apengda.springwebplus.starter.db.dialect.DataTypeConverter;
import com.github.apengda.springwebplus.starter.db.dialect.base.BaseDDLService;

public class MysqlDDLService extends BaseDDLService {
    public MysqlDDLService(DataTypeConverter dataTypeConverter) {
        super(dataTypeConverter);
    }
}
