package com.github.pdaodao.springwebplus.tool.io.pojo;

import lombok.Data;

@Data
public class CdcSetting {
    private Integer serverId = 6000;
    private CdcModel model = CdcModel.when_needed;
    private String timezone = "Asia/Shanghai";

    public enum CdcModel {
        always,
        initial_only,
        configuration_based,
        when_needed,
        initial,
        custom,
        no_data,
        recovery
    }
}
