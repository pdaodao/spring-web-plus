package com.github.pdaodao.springwebplus.tool.db.dialect.mysql;

import com.github.pdaodao.springwebplus.tool.db.dialect.base.BaseDbFunction;

public class MySqlDbFunction extends BaseDbFunction {

    @Override
    public String geoToTextFn() {
        return "ST_AsText";
    }

    @Override
    public String textToGeoFn() {
        return "ST_GeomFromText";
    }
}
