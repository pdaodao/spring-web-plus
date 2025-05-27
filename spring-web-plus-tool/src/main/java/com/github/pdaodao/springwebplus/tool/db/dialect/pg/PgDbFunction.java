package com.github.pdaodao.springwebplus.tool.db.dialect.pg;

import com.github.pdaodao.springwebplus.tool.db.dialect.base.BaseDbFunction;

public class PgDbFunction extends BaseDbFunction {
    @Override
    public String geoToTextFn() {
        return "ST_AsText";
    }

    @Override
    public String textToGeoFn() {
        return "ST_GeomFromText";
    }
}
