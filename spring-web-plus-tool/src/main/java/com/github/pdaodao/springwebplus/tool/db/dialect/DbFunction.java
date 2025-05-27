package com.github.pdaodao.springwebplus.tool.db.dialect;

public interface DbFunction {

    default String geoToTextFn(){
        return null;
    }

    default String textToGeoFn(){
        return null;
    }
}
