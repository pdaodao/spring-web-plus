package com.github.apengda.springwebplus.starter.db.dialect.sqlite;

import com.github.apengda.springwebplus.starter.db.dialect.DataTypeConverter;
import com.github.apengda.springwebplus.starter.db.dialect.base.BaseDDLService;

public class SqliteDDLService extends BaseDDLService {
    public SqliteDDLService(DataTypeConverter dataTypeConverter) {
        super(dataTypeConverter);
    }
}
